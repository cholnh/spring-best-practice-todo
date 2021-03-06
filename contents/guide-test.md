# Test 스타일 가이드
<br/><br/>



## :speech_balloon: 개요

해당 프로젝트에서 사용된 Test 스타일에 대해 설명합니다.  
(JUnit5 사용하여 테스트 코드를 작성합니다)

<br/><br/>


## 통합 테스트 전략

통합 테스트는 어플리케이션에서 사용되는 모든 Bean 을 등록하여 테스트를 진행하기에 실제 운영환경과 유사한 테스팅이 가능합니다.  
하지만 단위 테스트에 비해 소요시간이 오래 걸리며, 테스트 단위가 상대적으로 크기 때문에 디버깅이 어렵습니다.  
또한 테스트 동작 중 외부 API 호출은 Rollback 처리가 어려워 테스트 진행이 까다롭습니다.

<br/>

통합 테스트를 하기 위해 필요한 어노테이션과 Bean 을 담은 `Integration` base 클래스 입니다.  
base 클래스 상속을 통해 테스트 전략을 통일합니다.

```java
@SpringBootTest(classes = TodoApplication.class)
@AutoConfigureMockMvc
@Transactional
@Import(HttpEncodingAutoConfiguration.class)
@Disabled
public class IntegrationTest {
    @Autowired
    protected MockMvc mvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected WebApplicationContext webApplicationContext;
    ...
}
```

- `@AutoConfigureMockMvc`  
    MockMvc 클라이언트 사용을 위한 어노테이션입니다.  
    서블릿 컨테이너를 Mocking(실제 객체와 비슷한 모의 객체를 만드는 것)하여 실제 서블릿 컨테이너를 띄우지 않고,  
    모킹한 모조 객체를 MockUp(모킹한 객체를 메모리에서 얻어내는 과정)하여 띄우게 됩니다.  
    dispatcherServlet 이 만들어지긴 하지만 MockUp 된 모형 컨테이너를 사용하기 때문에 요청을 보내는 것'처럼' 테스트 진행이 가능합니다.
    
<br/>

- `@Transactional`  
    테스트 객체를 프록시로 감싸 로직 주위에 트랜젝션 시작과 커밋을 추가합니다.  
    이를 통해 테스트 내에서의 데이터베이스 조작을 자동으로 롤백시켜줍니다.

<br/>

- `@Import(HttpEncodingAutoConfiguration.class)`  
    CharacterEncodingFilter 를 추가하여 http 요청, 응답에 UTF-8 인코딩 적용합니다.  
    콘솔에 한글이 깨지는 것을 방지하기 위해 추가하였습니다.
    
<br/><br/>



## 슬라이스 테스트, 목 테스트 전략

레이어를 독립적으로 잘라 독립적으로 테스트하는 것을 슬라이스 테스트라 합니다.  
여러 레이어에 적합한 어노테이션을 사용하여 테스트합니다.

- `@WebMvcTest`
- `@WebFluxTest`
- `@DataJpaTest`
- `@JsonTest`
- `@RestClientTest`

<br/>

슬라이스 테스트 어노테이션을 사용하면 해당 레이어에서만 사용되는 관련 Bean 들만 등록해줍니다.  
따라서 통합 테스트에 비해 테스트 진행속도가 빠르고 디버깅이 쉬워집니다.

<br/>

예를들어, `@WebMvcTest` 어노테이션을 사용할 경우  
`@Controller`, `@ControllerAdvice`, `@JsonComponent`, `Converter`, `GenericConverter`,  
`Filter`, `WebMvcConfigurer`, `HandlerMethodArgumentResolver` 등을 Bean 으로 등록하게 됩니다.  
 
<br/>

이 외에 테스트에 필요하지 않은 `@Service`, `@Repository` 등은 등록하지 않습니다.  
딱 컨트롤러 레이어만 잘라 독립적으로 테스트할 수 있는 환경을 제공합니다.  
컨트롤러에서 사용되는 Service 레이어 는 `@MockBean` 어노테이션을 사용하여 의존성을 대체합니다.

<br/>

```java
class TodoApiTest extends ControllerTest {
    
    @MockBean
    private TodoQueryService todoQueryService;
    
    @Test
    @DisplayName("id에 해당하는 Todo 정보를 정상적으로 반환한다.")
    void find_byId_returnsJsonContainingTodoResponse() throws Exception {
    
        // given
        final Long id = 1L;
        final TodoResponse response = TodoResponse.builder().build();
        given(todoQueryService.findTodo(id))
                .willReturn(response);
    
        // when
        final ResultActions resultActions = mvc.perform(get("/todos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        
        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("createdDate").value(dateFormat(response.getCreatedDate())))
                .andExpect(jsonPath("lastModifiedDate").value(dateFormat(response.getLastModifiedDate())))
                .andExpect(jsonPath("active").value(response.getIsActive()))
                .andExpect(jsonPath("idx").value(response.getIdx()))
                .andExpect(jsonPath("title").value(response.getTitle()))
                .andExpect(jsonPath("contents").value(response.getContents()));
    }
}
```

위 테스트 코드에서 볼 수 있듯이 `given` 메서드를 사용하여 목 객체의 가짜 로직을 부여하였습니다.

```java
given(todoQueryService.findTodo(id))
                .willReturn(response);
``` 

<br/>

목 객체를 등록할 때 아래와 같은 방법도 쓰게됩니다.  

```java
@Mock
private TodoRepository mockTodoRepository;

@InjectMocks
private TodoCommandServiceImpl todoCommandServiceWithMock;
```

`TodoRepository` 클래스를 목(모조=가짜) 객체로 지정한 뒤, `TodoCommandServiceImpl` 서비스 내부에 주입하게 됩니다.  
서비스 객체는 정상적으로 동작하되 리포지토리 객체는 텅빈 목 객체로 주입받게 됩니다.

<br/>

이와 같이 Mock 객체를 사용하여 테스트할 경우, 빠른 테스트 속도와 외부 API 콜같은 까다로운 테스트 진행의 유연함이 장점이지만  
Mock 기반(실제가 아닌 모조)으로 테스트하기 때문에 실제 환경에서는 제대로 동작하지 않을 가능성이 매우 큽니다.

<br/><br/>



## Repository 테스트 전략

슬라이스 테스트 중 하나로 Repository 관련된 Bean 만 등록하여 진행하는 단위 테스트입니다.  
아래는 Repository 테스트에 사용되는 base 클래스 입니다.

```java
@DataJpaTest
@Import({ DataSourceConfiguration.class })
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Disabled
public class RepositoryTest {
}
```

- `@DataJpaTest`  
    Jpa 관련 설정만 스캔하도록 제한합니다.  
    기본적으로 인메모리 데이터베이스에 대한 테스트를 진행하게 됩니다.  

<br/>

- `@AutoConfigureTestDatabase`  
    실제 데이터베이스를 사용 하기위해 해당 어노테이션을 사용합니다.  
    replace 값이 `AutoConfigureTestDatabase.Replace.ANY` 인 경우 기본적으로 내장된 임베디드 데이터베이스를 사용하며,  
    `AutoConfigureTestDatabase.Replace.NONE` 인 경우 profile 에 등록된 데이터베이스 정보로 대체됩니다.
    
<br/>

- `@Import({ DataSourceConfiguration.class })`  
    DataSource 설정 Bean 을 주입 받습니다.

<br/>

Repository 테스트는 `JpaRepository` 에서 제공하는 기본 쿼리메서드(`findById`, `findByAll` 등)는 테스트 하지 않고,  
주로 커스텀하게 작성된 `SupportRepository` 를 테스트 하게 됩니다.

<br/><br/>



## Security 테스트 전략

주로 OAuth Authentication 관련된 내용을 테스트 하게 됩니다.  
`@WithMockUser` 어노테이션을 사용하여 요청에 OAuth 접근 권한을 부여합니다.  
각 권한 상태에 따른 HTTP 응답 Status 코드를 명시합니다.

```java
@Nested
@DisplayName("Todo 반환 테스트")
class findById {
    @Test
    @DisplayName("OAuth 권한이 없으면 id에 해당하는 Todo 정보 반환이 거부된다.")
    void findOne_unauthorizedToken_shouldFailWith401() throws Exception {
        requestFindOneTodo().andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username="test-todo-admin1", roles = "ADMIN")
    @DisplayName("Admin 권한은 id에 해당하는 Todo 정보를 정상적으로 반환한다.")
    void findOne_authorizedTokenAsAdmin_shouldSucceedWith200() throws Exception {
        requestFindOneTodo().andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="test-todo-user1", roles = "USER")
    @DisplayName("USER 권한은 id에 해당하는 Todo 정보를 정상적으로 반환한다.")
    void findOne_authorizedTokenAsUser_shouldSucceedWith200() throws Exception {
        requestFindOneTodo().andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GUEST")
    @DisplayName("Guest 권한은 id에 해당하는 Todo 정보를 정상적으로 반환한다.")
    void findOne_authorizedTokenAsGuest_shouldSucceedWith200() throws Exception {
        requestFindOneTodo().andExpect(status().isOk());
    }
}
```

<br/><br/>



## 메서드 네이밍

직관적인 테스트 메서드 네이밍은 테스트를 한 눈에 이해할 수 있게 합니다.  
cf) `@DisplayName` 어노테이션을 통해 메서드 네이밍을 통해 전달되지 못한 부족한 설명을 첨언할 수도 있습니다.

<br/>

테스트 이름에는 테스트 대상의 요구사항과 입력 값(시나리오), 그리고 상태에 대한 예상 결과가 포함되어야 합니다.

<br/>

**\[ MethodName_StateUnderTest_ExpectedBehavior \]**  

`Public void Sum_NegativeNumberAs1stParam_ExceptionThrown ()`

`Public void Sum_NegativeNumberAs2ndParam_ExceptionThrown ()`

`Public void Sum_simpleValues_Calculated ()`

`Public void Parse_OnEmptyString_ExceptionThrown ()`

`Public void Parse_SingleToken_ReturnsEqualToeknValue ()`