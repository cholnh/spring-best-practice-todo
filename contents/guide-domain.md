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
    
<br/>

- `@Lob`  
    관계형 데이터베이스 에서 varchar 를 넘어서는 큰 내용을 넣고 싶을 경우 사용합니다.  
    위 어노테이션 사용시 스프링이 추론하여 어떤 타입으로 저장할지를 판단하게 됩니다.  
    String 과 char 를 기본으로 하는 타입(@Clob)을 제외한 나머지는 @Blob 으로 사용되게 됩니다.  
    
<br/>

- `@Basic(fetch = FetchType.LAZY)`  
    fetch 전략과 optional 을 명시하기위해 어노테이션을 사용합니다.  
    (@Basic 의 경우 JPA Entities 에 적용되며, @Column 속성은 Databases 컬럼에 적용됩니다)  
    fetch 전략의 경우 해당 컬럼을 가져오는 과정을 `eagerly fetched` 또는 `lazily fetched` 으로 설정하게 됩니다.  
    `eagerly fetched` 의 경우 해당 필드 사용여부와 상관없이 객체에 로딩됩니다.  
    `lazily fetched` 의 경우 해당 필드가 사용되는 시점에 객체에 로딩됩니다.

<br/>

상위 부모 클래스인 `Auditable` 클래스에 대해 알아보겠습니다.  
해당 클래스는 감사 기능을 제공하는 공통 클래스입니다.  
`생성일자`, `수정일자`, `활성화여부` 등 엔티티 전반에 공통으로 사용되는 컬럼을 자동으로 넣어주게 됩니다.

```java
@EntityListeners(value = AuditingEntityListener.class)
@MappedSuperclass
@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class Auditable implements Serializable {

    @Column(name = "is_active", nullable = false, length = 1, columnDefinition = "VARCHAR(1) DEFAULT 'Y'")
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean isActive;

    @Column(name = "created_date", nullable = false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private LocalDateTime createdDate;

    @Column(name = "last_modified_date", nullable = false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @UpdateTimestamp
    private LocalDateTime lastModifiedDate;
}
```

- `@EntityListeners(value = AuditingEntityListener.class)`  
    엔티티의 변경(영속/업데이트 시)을 감지하면 해당 Listener(`AuditingEntityListener.class`) 를 실행합니다.  
    `AuditingEntityListener` 는 JPA 에서 제공하는 감사 기능을 제공하는 Listener 입니다.  
    
<br/>

- `@MappedSuperClass`  
    위 어노테이션을 사용하면 상속을 통해 엔티티에 해당 필드를 추가할 수 있습니다.  

<br/>

- `@CreationTimestamp`  
    생성 시간을 자동 입력합니다.
    
<br/>

- ` @UpdateTimestamp`  
    수정 시간을 자동 입력합니다.
    
<br/>

- `@Convert(converter = BooleanToYNConverter.class)`  
    관계형 데이터베이스에는 boolean 타입이 저장되지 않습니다.  
    따라서 `Y` 또는 `N` 문자열을 저장하여 논리연산자를 표현합니다.  
    이때 변환하기 위해 사용되는 Converter 입니다.
    
<br/>

- `columnDefinition`  
    `@Column` 어노테이션 속성 중 하나인 `columnDefinition` 은 컬럼의 기본값을 지정해줍니다.  
    `TIMESTAMP DEFAULT CURRENT_TIMESTAMP` 은 TIMESTAMP 기본값을 현재 시간으로 저장합니다.

<br/><br/>



## 임베디드 타입

JPA 에서는 새로운 값 타입(VO)을 직접 정의해서 사용할 수 있는데 이것을 임베디드 타입이라 합니다.  
(직접 정의한 임베디드 타입은 int, String 처럼 값 타입이 되게 됩니다)

<br/>

`@Embedded` 와 `@Embeddable` 을 통해 도메인 객체의 책임을 나눌 수 있습니다.  
임베디드 타입을 포함한 모든 값(VO) 타입은 엔티티의 생명주기에 의존하게 됩니다.  

<br/>

예시를 보겠습니다.  

```java
@Data
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED) 
public class Password {

    @Column(name = "password_value", nullable = false, length = 255)
    @JsonIgnore
    private String passwordValue;

    @Column(name = "password_failed_count", nullable = false, columnDefinition = "INT default 0")
    @PositiveOrZero
    private int failedCount;

    @Builder
    public Password(String passwordValue, @PositiveOrZero int failedCount) {
        this.passwordValue = passwordValue;
        this.failedCount = failedCount;
    }
}
```

값 객체로 정의할 클래스 상단에 `@Embeddable` 를 선언합니다.  
사용하는 쪽 필드엔 `@Embedded` 를 선언합니다.  

<br/>

위와 같이 임베디드 타입을 사용하게 되면 코드의 응집력이 증가될 뿐만 아니라 책임이 고르게 분산되고  
코드 중복을 방지하며 추후 테스트 코드 작성에 편리하게 됩니다.

<br/><br/>



## Rich Object

객체지향에서 중요한 것들이 많겠지만 그중에 하나가 객체 본인의 책임을 다하는 것입니다.  
객체가 자기 자신의 책임을 다하지 않으면 그 책임은 다른 객체에게 넘어가게 됩니다.

<br/>

도메인 객체들에 기본적인 getter, setter 외에는 메서드를 작성하지 않는 경우가 있습니다.  
이렇게 되면 객체 본인의 책임을 다하지 않으니 이런 책임들이 다른 객체에서 이루어지게 됩니다.  
위에서 언급하였듯이 그 책임은 다른 객체에게 넘어가게 되고 코드의 응집력은 망가질 것입니다.

<br/>

쿠폰 도메인 객체 예시입니다.

```java
public class Coupon {

    @Embedded
    private CouponCode code;

    @Column(name = "used", nullable = false)
    private boolean used;

    @Column(name = "discount", nullable = false)
    private double discount;

    @Column(name = "expiration_date", nullable = false, updatable = false)
    private LocalDate expirationDate;

    public boolean isExpired() {
        return LocalDate.now().isAfter(expirationDate);
    }

    public void use() {
        verifyExpiration();
        verifyUsed();
        this.used = true;
    }

    private void verifyUsed() {
        if (used) throw new CouponAlreadyUseException();
    }

    private void verifyExpiration() {
        if (LocalDate.now().isAfter(getExpirationDate())) throw new CouponExpireException();
    }
}
```

단순하게 getter, setter 메서드만 제공하지 않고, 쿠폰 도메인에 관한 비즈니스 로직이 응집되어 메서드로 표현되었습니다.  

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
JPA 트렌젝션 범위를 벗어나는 경계(컨트롤러 레이어)에서는 Domain Model 이 준영속 상태가 되어 지연로딩의 이점을 활용할 수 없기 때문입니다.  
(OSIV 를 통한 Nontransactional reads 를 하더라도 프레젠테이션 레이어에서 값이 변경되고 트랜젝션을 다시 시작할 경우 변경이 반영될 수 있씁니다)  
또한 컨트롤러 레이어에서의 Domain Model 변환 처리는 input/output 에 대한 명확성이 떨어지는 단점이 있기 때문입니다.  

<br/>

원칙적으론 여러 외부 어댑터를 지원해야 하는 헥사고날 아키텍처에서의 서비스 레이어는 공통된 비즈니스 Model 을 사용하는 것이 마땅합니다.  
서비스 레이어의 DTO 사용은 의존성이 안쪽(컨트롤러에서 서비스, 서비스에서 리포짓토리)으로 향해야 하는 아키텍처에서 그 흐름을 거스르기 때문입니다.
하지만 보통 여러 종류의 컨트롤러가 하나의 서비스를 사용하기 보단 한 종류의 컨트롤러가 서비스를 사용하기 때문에  
서비스 레이어 까지 DTO 진입을 허용하여 변환하여 사용하거나, transform 레이어를 중간에 추가한 4-tier 전략으로 가기도 합니다.  

<br/>

프로젝트 상황과 아키텍처를 고려하여 dto 진입 범위를 정하는것이 바람직할 것 같습니다.  
(그렇다고 Repository 레이어까지 DTO 가 내려갈 일은 없게 해야 합니다)