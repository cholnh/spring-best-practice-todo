# Domain Model 및 범위 가이드

<br/><br/>



## :speech_balloon: 개요

해당 프로젝트에서 다루는 Domain Model 에 대해 설명하며 Domain Model 의 책임 범위에 대해 알아보겠습니다.

<br/><br/>




## Domain Model

도메인은 비즈니스 개념을 표현하고, 서비스는 도메인을 활용해 시스템 흐름 처리를 수행합니다.  
여기서 중요한 점은 서비스는 흐름 처리를 수행할 뿐 비즈니스 로직은 도메인 객체 스스로가 책임을 다해야 합니다.  

<br/>

### Domain Model 예시

프로젝트 Entity 중 `Todo` 클래스에 관한 설명입니다.  
저장소 처리 어댑터로 JPA(Java Persistence Api)를 사용합니다.

```java
@Entity
@Table(name = "todo_tbl")
@Getter
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Todo extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(name="title", length = 100, nullable = false)
    private String title;

    @Column(name = "contents", nullable = false)
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String contents;
}
```

우선 위쪽에 정의된 어노테이션을 살펴보겠습니다. 

- `@Entity`  
    ORM 관련 기술 중 Java 순정 기술로 만들어진 JPA 를 사용하며, 해당 클래스가 Entity 클래스 라는 것을 명시합니다.  
    엔티티는 테이블의 구조를 Java 클래스로 재구성하게 해주며, 내부 데이터들은 엔티티의 인스턴스로 처리됩니다.  
    (테이블 = 클래스, 컬럼 = 필드)  
    
<br/>

- `@Table(name = "todo_tbl")`  
    엔티티로 정의된 클래스에서 테이블 요구 사항을 명시해줍니다.  
    + `name` 속성은 테이블 이름을 명시해주며 실제 관계형 데이터베이스 테이블의 이름으로 매핑됩니다.  
    + `schema` 속성은 테이블의 스키마를 명시합니다.

<br/>

- `@Getter`  
    Lombok(컴파일 시점에서 특정 어노테이션으로 여러 코드를 추가할 수 있는 라이브러리)의 Getter 메서드를 추가하는 어노테이션입니다.  
    해당 엔티티 클래스는 비즈니스 규칙에 따라서만 변경되어야 합니다. 따라서 의도가 분명하지 않은 객체 변경을 보호해야 합니다.  
    Lombok 에서 제공하는 `@Setter` (`@Data` 어노테이션 또한 내부에 `@Setter` 가 포함됩니다) 어노테이션은 내부 객체를 변경하는 코드를 삽입합니다.  
    객체의 안전성이 보장되지 않는 코드이므로 최대한 지양해야합니다.  
    (비즈니스 규칙에 의해) 내부 필드의 변경이 필요할 경우 명시적인 메서드를 만들어 제공하는 것이 바람직합니다.  

<br/>

- ~~`@DynamicUpdate`~~
    필드의 값 중 null 값을 갖는 필드를 update 쿼리에서 자동으로 제외하는 어노테이션입니다.  
    (Dirty Checking 으로 엔티티 객체 변화를 감지 후 SQL 문을 생성합니다)
    즉, 실제 값이 변경된 필드만 변경되게 하는 기능이지만 성능 오버헤드(엔티티 상태 추적, SQL 캐시 사용 불가)가 있으므로 고려가 필요합니다.  
    필드가 많을 경우(정규화가 잘못돼 있을 확률이 높습니다) 또는 버전(`@Version`)을 사용하지 않는 엔티티의  
    Optimistic Lock (낙관적 락)을 사용하고자 할 경우 라면 `@DynamicUpdate` 를 사용하는 것이 좋습니다.  
    (버전을 사용하지 않고 모든 필드를 Optimistic Lock 조건으로 걸게되면 어떤게 Dirty 필드인지 체크해서 where 조건을 만들 필요가 있기 때문입니다)  
    -> 사용하지 않는 어노테이션으로 변경되었습니다.
    
<br/>

- `@NoArgsConstructor(access = AccessLevel.PROTECTED)`  
    JPA 에서는 Proxy 생성을 위해 기본 생성자를 반드시 생성해야 합니다.  
    또한 기본 생성자의 접근 제한을 private 으로 설정하면 추후 Lazy loading 시 Proxy 에서 해당 엔티티를 생성하지 못하는 Exception 이 발생하게 됩니다.
    `@NoArgsConstructor` 어노테이션은 이름 그대로 arguments 가 필요없는 기본 생성자 코드를 주입합니다.  
    외부에서 해당 엔티티 생성을 열어둘 필요가 없으므로 protected 권한으로 생성자를 설정하는 것이 바람직합니다.  
    + `access` 속성은 생성자의 접근 권한을 명시합니다. `AccessLevel` 열거형 타입을 통해 권한 레벨을 조절할 수 있습니다.
    
<br/>

- `@SuperBuilder`  
    상위 부모 클래스인 `Auditable` 클래스의 생성자 `Builder` 를 추가합니다.  
    즉, 생성자 빌더 패턴 코드를 생성해주는 `@Builder` 어노테이션에 부모 클래스의 생성자 빌더 코드가 추가된 어노테이션입니다.  
    부모 클래스에도 `@SuperBuilder` 어노테이션이 달려있어야 합니다.
    

<br/><br/>



## Domain Model 의 책임 범위

일반적인 헥사고날 아키텍처는 다음과 같은 구조를 갖습니다.  

- (외부 영역) Controller 레이어
- (내부 영역) Service 레이어
- (내부 영역) Repository 레이어

위 구조에서 Domain Model 의 책임은 어느 범위까지 노출되어야 할까요?  

<br/>

외부랑 통신을 하게되는 외부 영역 어댑터에서는 Domain Model 대신 DTO 를 통해 통신하게 됩니다.  
내부 비즈니스 로직을 담당하는 Domain Model 의 책임이 외부 영역까지 확장될 필요가 없을 뿐더러(비즈니스 로직은 트렌젝션 내에서만 해결),  
Domain Model 을 통해 외부랑 통신을 할 경우 내부 비즈니스 규칙이 노출 될 위험이 있기 때문입니다.  

<br/>

서비스 레이어에서는 컨트롤러에서 넘겨진 DTO 를 Domain Model 으로 변환하는 작업을 거칩니다.  
서비스 레이어에서 DTO 를 받는 까닭은 위 설명과 같이 Domain Model 의 책임이 외부 영역까지 확장될 필요가 없을 뿐더러,  
컨트롤러 레이어에서의 Domain Model 변환 처리는 input/output 에 대한 명확성이 떨어지는 단점이 있기 때문입니다.  


<br/>

원칙적으론 여러 외부 어댑터를 지원해야 하는 헥사고날 아키텍처에서의 서비스 레이어는 공통된 비즈니스 Model 을 사용하는 것이 마땅합니다.  
서비스 레이어의 DTO 사용은 의존성이 안쪽(컨트롤러에서 서비스, 서비스에서 리포짓토리)으로 향해야 하는 아키텍처에서 그 흐름을 거스르기 때문입니다.
하지만 보통 여러 종류의 컨트롤러가 하나의 서비스를 사용하기 보단 한 종류의 컨트롤러가 서비스를 사용하기 때문에  
서비스 레이어 까지 DTO 진입을 허용하여 변환하여 사용하거나, transform 레이어를 중간에 추가한 4-tier 전략으로 가기도 합니다.  

<br/>

