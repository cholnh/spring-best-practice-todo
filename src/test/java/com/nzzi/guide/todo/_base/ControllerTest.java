package com.nzzi.guide.todo._base;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

// 컨트롤러 슬라이스 테스트를 위한 설정을 제공해주는 어노테이션
// 컨트롤러 관련 Bean 만 등록됩니다.
@WebMvcTest

// CharacterEncodingFilter 를 추가하여 http 요청, 응답에 인코딩 적용 (UTF-8)
@Import(HttpEncodingAutoConfiguration.class)

// 실제로 동작할 필요가 없으니 해당 어노테이션 추가
@Disabled
public class ControllerTest {

    // 컨트롤러 테스트 시 필요한 기능들을 protected 로 제공합니다. (유틸성 클래스들을 제공하면 편리합니다)

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected WebApplicationContext webApplicationContext;
}
