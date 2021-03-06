# DAO 스타일 가이드
<br/><br/>



## :speech_balloon: 개요

해당 프로젝트에서 사용된 DAO 구조 및 코드 스타일에 대해 설명합니다.

<br/><br/>


## DATA REST

Spring Data Jpa 를 사용하기위해 `@RepositoryRestResource` 어노테이션을 넣어줍니다.  
위 어노테이션은 `@Repository` 를 포함하며, 해당 리포지토리를 사용할 수 있는 RESTful 한 API 를 자동으로 생성합니다.  
(Spring Data Jpa 에서 제공하는 API 는 REST 충족 조건인 Hypermedia Controls 을 만족하기 위해 HATEOAS 사용합니다)

<br/>

Spring Data Jpa 의 application.yml 설정은 다음과 같습니다.

```yaml
data:
  rest:
    base-path: /jpa
    detection-strategy: default
```

- `base-path` 는 REST API 기본 url 을 명시합니다.
- `detection-strategy` 는 프로젝트 내의 RepositoryRestResource 중에 어느 리포지토리를 노출할 지에 대한 규칙을 명시합니다.

<br/><br/>



## SupportRepository

해당 프로젝트에서는 리포지토리 구성을 Jpa 를 사용하는 `DefaultRepository` 와 QueryDSL 를 사용하는 `SupportRepository` 로 나누고  
기본 리포지토리에 `SupportRepository` 를 다중상속하여 사용합니다.  

<br/>

- 기본 Repository 인터페이스  

    ```java
    @RepositoryRestResource(exported = true)
    public interface TodoRepository extends JpaRepository<Todo, Long>, TodoSupportRepository {
        List<Todo> searchByTitleContains(String query);
    }
    ```
    
    내부 인터페이스 메서드로 Jpa 쿼리 메서드를 넣어 사용합니다.  
    (Jpa 쿼리 메서드: 메소드 이름을 분석해서 JPQL 쿼리 실행)  
    
    <br/>
    
    Jpa 사용을 위한 JpaRepository 와 QueryDSL 사용을 위한 SupportRepository 를 다중상속합니다.
    

<br/>

- SupportRepository 인터페이스  

    ```java
    public interface TodoSupportRepository {
        Page<Todo> searchByContents(TodoPredicate predicate, Pageable pageable);
    }
    ```
    
    QueryDSL 를 사용하는 메서드가 명시되어있는 인터페이스 입니다.  
    
    <br/>
    
    Querydsl 이란 정적 타입을 이용해서 sql 과 같은 쿼리를 자동으로 생성해주는 프레임워크 입니다.  
    자바 문법을 사용하기 때문에 안전한 참조와 복잡한 쿼리문 작성이 가능하며, 코드 자동 완성 기능을 사용하여 생산성이 높아집니다.  
    또한 동적으로 쿼리를 생성할 수 있습니다.
    
<br/>

- SupportRepository 구현체  

    ```java
    @Transactional(readOnly = true)
    public class TodoSupportRepositoryImpl extends QuerydslRepositorySupport implements TodoSupportRepository {
    
        public TodoSupportRepositoryImpl() {
            super(Todo.class);
        }
    
        @Override
        public Page<Todo> searchByContents(TodoPredicate predicate, Pageable pageable) {
    
            final QTodo qTodo = QTodo.todo;
    
            List<Todo> result =
                    from(qTodo)
                    .select(qTodo)
                    .where(predicate.build()
                    .and(qTodo.isActive.isTrue()))
                    .fetch();
    
            return new PageImpl<>(result, pageable, result.size());
        }
    }
    ```
    
    `QuerydslRepositorySupport` 를 상속받는 `SupportRepository` 인터페이스의 구현체입니다.  
    
    <br/>
    
    구현체는 `QuerydslRepositorySupport` 를 상속받아야 하며 클래스명은 인터페이스 이름 + `Impl` 로 작성해야 합니다.  
    또한 super 생성자에 도메인 클래스를 인자로 넘겨줘야 합니다.  
    
    ```java
    public class TodoSupportRepositoryImpl extends QuerydslRepositorySupport implements TodoSupportRepository {
        
        public TodoSupportRepositoryImpl() {
            super(Todo.class);
        }
    }
    ```
    
    <br/>
    
    Q도메인(QueryDSL 전용 객체)을 이용하여 메서드 체이닝 방식으로 쿼리문을 작성합니다.  
    `QuerydslRepositorySupport` 에서 제공하는 기본 쿼리 메서드(`from` 과 같은 이니셜라이저 메서드)를 사용합니다.  
    
    <br/>
    
    `QuerydslRepositorySupport` 기본 쿼리 메서드는 기존 쿼리(Select 로 시작하는)와는 다르게 `from` 으로 시작합니다.  
    가독성 좋게 `select` 로 시작하는 체이닝 메서드를 사용하고 싶으시면, 다음과 같은 `JPAQueryFactory` 빈을 만듭니다.  
    
    ```java
    @Configuration
    public class QuerydslConfiguration {
    
        @PersistenceContext
        private EntityManager entityManager;
    
        @Bean
        public JPAQueryFactory jpaQueryFactory() {
            return new JPAQueryFactory(entityManager);
        }
    }
    ```
    
    `JPAQueryFactory` 에서는 `select` 또는 `selectFrom` 과 같은 메서드를 제공합니다.  
    구현 클래스에 해당 빈을 주입받아 사용하면 됩니다.