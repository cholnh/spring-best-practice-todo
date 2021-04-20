package com.nzzi.guide.todo.domain.todo.service.impl;

import com.nzzi.guide.todo.domain.todo.dao.jpa.TodoRepository;
import com.nzzi.guide.todo.domain.todo.dto.TodoRequest;
import com.nzzi.guide.todo.domain.todo.dto.TodoResponse;
import com.nzzi.guide.todo.domain.todo.model.Todo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest
class TodoCommandServiceImplTest {

    @Mock
    private TodoRepository mockTodoRepository;

    @InjectMocks
    private TodoCommandServiceImpl todoCommandServiceWithMock;

    @Nested
    class TodoCRUD {

        @Test
        @DisplayName("Todo 정보를 정상적으로 생성한다.")
        void create_todo_success() {

            // given
            final Long expectedId = 1L;
            final String expectedTitle = "타이틀";
            final String expectedContents = "콘텐츠";
            TodoRequest expectedTodoRequest = TodoRequest.of(
                    expectedTitle,
                    expectedContents);
            given(mockTodoRepository.save(any()))
                    .willReturn(mockEntity(
                            expectedId,
                            expectedTitle,
                            expectedContents));

            // when
            TodoResponse actualTodoResponse = todoCommandServiceWithMock
                    .create(expectedTodoRequest);

            // then
            assertNotNull(actualTodoResponse.getIdx());
            assertNotNull(actualTodoResponse.getCreatedDate());
            assertNotNull(actualTodoResponse.getLastModifiedDate());
            assertTrue(actualTodoResponse.isActive());
            assertEquals(expectedId, actualTodoResponse.getIdx());
            assertEquals(expectedTodoRequest.getTitle(), actualTodoResponse.getTitle());
            assertEquals(expectedTodoRequest.getContents(), actualTodoResponse.getContents());
        }

        @Test
        @DisplayName("id에 해당하는 Todo 정보를 정상적으로 수정한다.")
        void update_todo_success() {

            // given
            final Long expectedId = 1L;
            TodoRequest beforeTodoRequest = TodoRequest.of(
                    "업데이트 전 타이틀",
                    "업데이트 전 컨텐츠");
            TodoRequest expectedTodoRequest = TodoRequest.of(
                    "업데이트 후 타이틀",
                    "업데이트 후 컨텐츠");
            given(mockTodoRepository.findById(expectedId))
                    .willReturn(Optional.of(beforeTodoRequest.toEntity()));
            given(mockTodoRepository.save(any()))
                    .willReturn(mockEntity(
                            expectedId,
                            expectedTodoRequest.getTitle(),
                            expectedTodoRequest.getContents()));

            // when
            TodoResponse actualTodoResponse = todoCommandServiceWithMock
                    .update(expectedId, expectedTodoRequest);

            // then
            assertNotNull(actualTodoResponse.getIdx());
            assertNotNull(actualTodoResponse.getCreatedDate());
            assertNotNull(actualTodoResponse.getLastModifiedDate());
            assertTrue(actualTodoResponse.isActive());
            assertEquals(expectedId, actualTodoResponse.getIdx());
            assertEquals(expectedTodoRequest.getTitle(), actualTodoResponse.getTitle());
            assertEquals(expectedTodoRequest.getContents(), actualTodoResponse.getContents());
        }

        @Test
        @DisplayName("id에 해당하는 Todo 정보를 정상적으로 삭제한다.")
        void delete_todo_success() {

            // given
            final Long expectedId = 1L;
            Todo expectedMockEntity = mockEntity(expectedId);
            given(mockTodoRepository.findById(expectedId))
                    .willReturn(Optional.of(expectedMockEntity));

            // when
            TodoResponse actualTodoResponse = todoCommandServiceWithMock
                    .delete(expectedId);

            // then
            verify(mockTodoRepository).delete(expectedMockEntity);
            assertNotNull(actualTodoResponse.getIdx());
            assertNotNull(actualTodoResponse.getCreatedDate());
            assertNotNull(actualTodoResponse.getLastModifiedDate());
            assertTrue(actualTodoResponse.isActive());
            assertEquals(expectedId, actualTodoResponse.getIdx());
        }

        private Todo mockEntity(Long id) {
            return Todo.builder()
                    .isActive(true)
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .idx(id)
                    .title("dummy title")
                    .contents("dummy contents")
                    .build();
        }

        private Todo mockEntity(Long id, String title, String contents) {
            return Todo.builder()
                    .isActive(true)
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .idx(id)
                    .title(title)
                    .contents(contents)
                    .build();
        }
    }
}