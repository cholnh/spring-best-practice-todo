package com.nzzi.guide.todo._base;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

// mockito 사용을 위한 extension 어노테이션
@ExtendWith(MockitoExtension.class)

// 실제로 동작할 필요가 없으니 해당 어노테이션 추가
@Disabled
public class MockTest {
}
