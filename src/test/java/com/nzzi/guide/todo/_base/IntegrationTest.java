package com.nzzi.guide.todo._base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nzzi.guide.todo.TodoApplication;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

// 통합 테스트용 base 클래스 (base 클래스 상속을 통해 테스트 전략 통일)
// 주로 컨트롤러 테스트를 통해 전체 플로우(요청부터 응답까지)를 테스팅합니다.
// Bean 전체를 스캔하기 때문에 운영환경과 가장 유사하게 테스트 가능하나,
// 테스트 시간이 오래 걸리고 테스트 단위가 크기 때문에 디버깅이 어려우며
// 외부 API 콜 같은 롤백 처리가 안되는 테스트 진행이 어렵습니다.
@SpringBootTest(classes = TodoApplication.class)

// MockMvc 클라이언트 사용을 위한 어노테이션입니다.
// Mock 타입은 서블릿 컨테이너(톰캣 같은)를 테스트용으로 띄우지 않고,
// Mockup 을 해서 서블릿을 Mocking 한 것을 띄워줍니다.
// (dispatcherServlet 이 만들어지긴 하는데 Mockup 이 되어, 요청을 보내는 것 '처럼' 테스트)
@AutoConfigureMockMvc

// 테스트 객체를 프록시로 감싸 로직 주위에 트랜젝션 시작과 커밋을 추가합니다.
// 테스트를 통한 데이터베이스 조작을 자동으로 롤백시켜 줍니다.
@Transactional

// CharacterEncodingFilter 를 추가하여 http 요청, 응답에 인코딩 적용 (UTF-8)
@Import(HttpEncodingAutoConfiguration.class)

// 실제로 동작할 필요가 없으니 해당 어노테이션 추가
@Disabled
public class IntegrationTest {

    // 통합 테스트 시 필요한 기능들을 protected 로 제공합니다. (유틸성 클래스들을 제공하면 편리합니다)
    // MockMVC 클라이언트 테스트 사용을 위한
    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected WebApplicationContext webApplicationContext;
}
