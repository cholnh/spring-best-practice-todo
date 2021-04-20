package com.nzzi.guide.todo.domain.todo.service.impl;

import com.nzzi.guide.todo.domain.todo.dao.jpa.TodoRepository;
import com.nzzi.guide.todo.domain.todo.dto.TodoPredicate;
import com.nzzi.guide.todo.domain.todo.dto.TodoResponse;
import com.nzzi.guide.todo.domain.todo.exception.TodoNotFoundException;
import com.nzzi.guide.todo.domain.todo.model.Todo;
import com.nzzi.guide.todo.domain.todo.service.TodoQueryService;
import com.nzzi.guide.todo.global.error.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class TodoQueryServiceImplTest {

    @Autowired
    private TodoQueryService todoQueryService;

    @Mock
    private TodoRepository mockTodoRepository;

    @InjectMocks
    private TodoQueryServiceImpl todoQueryServiceWithMock;

    @BeforeEach
    void setUp() {
    }

    @Nested
    class TodoFind {

        @Test
        @DisplayName("Todo 정보를 정상적으로 가져온다.")
        void find_todo_success() {

            // given (해당 테스트는 mock 사용할 필요 없지만 예시를 위해 사용)
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
            TodoResponse actualTodoResponse = todoQueryServiceWithMock.findTodo(expectedId);

            // then
            assertNotNull(actualTodoResponse.getCreatedDate());
            assertNotNull(actualTodoResponse.getLastModifiedDate());
            assertNotNull(actualTodoResponse.getIdx());
            assertNotNull(actualTodoResponse.getTitle());
            assertNotNull(actualTodoResponse.getContents());
            assertEquals(expectedId, actualTodoResponse.getIdx());
        }

        @Test
        @DisplayName("등록되지 않은 Todo 조회는 예외를 발생시킨다.")
        void find_todo_fail() {

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
        @DisplayName("Todo 목록 전체를 page 형태로 가져온다.")
        void find_all_todo_success() {

            // given
            Pageable pageable = Pageable.unpaged();

            // when
            Page<TodoResponse> actualPage = todoQueryService.findTodos(pageable);

            // then
            actualPage.forEach(actualTodo -> {
                assertNotNull(actualTodo.getCreatedDate());
                assertNotNull(actualTodo.getLastModifiedDate());
                assertNotNull(actualTodo.getIdx());
                assertNotNull(actualTodo.getTitle());
                assertNotNull(actualTodo.getContents());
            });
        }

        @Test
        @DisplayName("size, page 가 정해진 Todo 목록을 page 형태로 가져온다.")
        void find_all_todo_with_page_success() {

            // given
            int expectedPage = 0;
            int expectedSize = 10;
            Sort.Direction expectedDirection = Sort.Direction.DESC;
            String expectedProperty = "idx";

            // when
            Page<TodoResponse> actualPage = todoQueryService.findTodos(
                    PageRequest.of(expectedPage, expectedSize,
                            Sort.by(expectedDirection, expectedProperty)));

            // then
            assertEquals(expectedPage, actualPage.getPageable().getPageNumber());
            assertEquals(expectedSize, actualPage.getPageable().getPageSize());
            assertTrue(actualPage.getSort().isSorted());
        }

        @Test
        @DisplayName("각 필드값으로 검색된 Todo 정보를 가져온다.")
        void search_todo_success() {

            // given
            String expectedSearchTitle = "테스트";
            String expectedSearchContents = "내용";

            // when
            Page<TodoResponse> actualPageSearchedTitle = todoQueryService.searchByContents(
                    TodoPredicate.of("title=" + expectedSearchTitle),
                    Pageable.unpaged());
            Page<TodoResponse> actualPageSearchedContents = todoQueryService.searchByContents(
                    TodoPredicate.of("contents=" + expectedSearchContents),
                    Pageable.unpaged());

            // then
            actualPageSearchedTitle.forEach(actualTodo -> {
                assertNotNull(actualTodo.getCreatedDate());
                assertNotNull(actualTodo.getLastModifiedDate());
                assertNotNull(actualTodo.getIdx());
                assertNotNull(actualTodo.getTitle());
                assertNotNull(actualTodo.getContents());
                assertTrue(actualTodo.getTitle().contains(expectedSearchTitle));
            });
            actualPageSearchedContents.forEach(actualTodo -> {
                assertNotNull(actualTodo.getCreatedDate());
                assertNotNull(actualTodo.getLastModifiedDate());
                assertNotNull(actualTodo.getIdx());
                assertNotNull(actualTodo.getTitle());
                assertNotNull(actualTodo.getContents());
                assertTrue(actualTodo.getContents().contains(expectedSearchContents));
            });
        }
    }
}