package com.nzzi.guide.todo._base;

import com.nzzi.guide.todo.global.configuration.database.DataSourceConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

// Repository 단위 테스트
// Jpa 관련 설정만 스캔하도록 제한하여, 보다 가벼운 테스팅 가능.
@DataJpaTest

// DataSourceConfiguration 클래스를 불러와 dataSource 빈을 등록합니다.
@Import({ DataSourceConfiguration.class })

// 테스트용 데이터베이스를 설정합니다.
// 기본값은 ANY(설정파일에 설정된 Embedded DataSource 사용) 이고,
// 실제 디비에 테스트하기를 원할 경우 NONE 을 설정합니다.
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)

// @DataJpaTest 내부에 @Transactional 이 설정되어 있어 기본적인 트랜젝션을 제공하지만,
// 트랜젝션 기능이 필요하지 않을 경우 Propagation.NOT_SUPPORTED 를 설정합니다.
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class RepositoryTest {
}