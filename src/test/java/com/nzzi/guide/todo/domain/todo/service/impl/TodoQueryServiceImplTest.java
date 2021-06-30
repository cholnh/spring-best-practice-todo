package com.nzzi.guide.todo.domain.todo.service.impl;

import com.nzzi.guide.todo._base.MockTest;
import com.nzzi.guide.todo.domain.todo.dao.jpa.TodoRepository;
import com.nzzi.guide.todo.domain.todo.dto.TodoPredicate;
import com.nzzi.guide.todo.domain.todo.dto.TodoResponse;
import com.nzzi.guide.todo.domain.todo.exception.TodoNotFoundException;
import com.nzzi.guide.todo.domain.todo.model.Todo;
import com.nzzi.guide.todo.domain.todo.model.TodoBuilder;
import com.nzzi.guide.todo.global.error.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TodoQueryServiceImplTest extends MockTest {

    @Mock
    private TodoRepository mockTodoRepository;

    @InjectMocks
    private TodoQueryServiceImpl todoQueryServiceWithMock;

    @Nested
    class TodoFind {

        @Test
        @DisplayName("Todo 정보를 정상적으로 가져온다.")
        void find_todo_shouldSucceed() {

            // given (해당 테스트는 mock 사용할 필요 없지만 예시를 위해 사용)
            final Todo savedTodo = TodoBuilder.build(1L, "제목", "내용");
            given(mockTodoRepository.findById(savedTodo.getIdx()))
                    .willReturn(Optional.of(savedTodo));

            // when
            TodoResponse actualTodoResponse = todoQueryServiceWithMock
                    .findTodo(savedTodo.getIdx());

            // then
            assertNotNull(actualTodoResponse.getCreatedDate());
            assertNotNull(actualTodoResponse.getLastModifiedDate());
            assertNotNull(actualTodoResponse.getIdx());
            assertNotNull(actualTodoResponse.getTitle());
            assertNotNull(actualTodoResponse.getContents());
        }

        @Test
        @DisplayName("등록되지 않은 Todo 조회는 예외를 발생시킨다.")
        void find_uncreatedTodo_shouldFail() {

            // given
            final Long idForExceptionExpected = -1L;

            // when
            TodoNotFoundException expectedException = assertThrows(
                    TodoNotFoundException.class,
                    () -> todoQueryServiceWithMock.findTodo(idForExceptionExpected));

            // then
            assertEquals(expectedException.getErrorCode(), ErrorCode.ENTITY_NOT_FOUND);
            assertEquals(expectedException.getMessage(), "not found");
        }

        @Test
        @DisplayName("Todo 목록 전체를 page 형태로 가져온다.")
        void findAll_todoList_shouldSucceed() {

            // given
            Pageable pageable = mock(PageRequest.class);
            given(mockTodoRepository.findAll(pageable))
                    .willReturn(Page.empty());

            // when
            todoQueryServiceWithMock.findTodos(pageable);

            // then
            verify(mockTodoRepository).findAll(pageable);
        }

        @Test
        @DisplayName("각 필드값으로 검색된 Todo 정보를 가져온다.")
        void search_todo_shouldSucceed() {

            // given
            final TodoPredicate predicate = mock(TodoPredicate.class);
            final PageRequest pageable = mock(PageRequest.class);
            given(mockTodoRepository.search(predicate, pageable))
                    .willReturn(Page.empty());

            // when
            todoQueryServiceWithMock.search(predicate, pageable);

            // then
            verify(mockTodoRepository).search(predicate, pageable);
        }
    }
}