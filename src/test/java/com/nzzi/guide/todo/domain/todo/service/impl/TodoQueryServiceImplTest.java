package com.nzzi.guide.todo.domain.todo.service.impl;

import com.nzzi.guide.todo.domain.todo.dao.jpa.TodoRepository;
import com.nzzi.guide.todo.domain.todo.dto.TodoResponse;
import com.nzzi.guide.todo.domain.todo.exception.TodoNotFoundException;
import com.nzzi.guide.todo.domain.todo.model.Todo;
import com.nzzi.guide.todo.domain.todo.service.TodoQueryService;
import com.nzzi.guide.todo.global.error.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
class TodoQueryServiceImplTest {

    @Autowired
    private TodoQueryService todoQueryService;

    @Mock
    private TodoRepository mockTodoRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void Todo정보를_정상적으로_가져온다() {

        // given (해당 테스트는 mock 사용할 필요 없지만 예시를 위해 사용)
        TodoQueryService todoQueryServiceWithMock = new TodoQueryServiceImpl(mockTodoRepository);
        Long expectedId = 1L;
        Todo mockEntity = Todo.builder()
                .isActive(true)
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .idx(expectedId)
                .title("타이틀")
                .contents("콘텐츠")
                .build();
        when(mockTodoRepository.findById(expectedId))
                .thenReturn(Optional.ofNullable(mockEntity));

        // when
        TodoResponse actualTodo = todoQueryServiceWithMock.findTodo(expectedId);

        // then
        assertNotNull(actualTodo.getCreatedDate());
        assertNotNull(actualTodo.getLastModifiedDate());
        assertNotNull(actualTodo.getIdx());
        assertNotNull(actualTodo.getTitle());
        assertNotNull(actualTodo.getContents());
        assertEquals(expectedId, actualTodo.getIdx());
    }

    @Test
    void 등록되지않은_Todo조회는_예외를_발생시킨다() {

        // given
        Long idForExceptionExpected = -1L;

        // when
        TodoNotFoundException expectedException = assertThrows(TodoNotFoundException.class,
                () -> todoQueryService.findTodo(idForExceptionExpected));

        // then
        assertEquals(expectedException.getErrorCode(), ErrorCode.ENTITY_NOT_FOUND);
        assertEquals(expectedException.getMessage(), "not found");
    }

    @Test
    void Todo목록을_정상적으로_가져온다() {


    }

    @Test
    void 컨텐츠로_검색된_Todo정보를_가져온다() {

    }
}