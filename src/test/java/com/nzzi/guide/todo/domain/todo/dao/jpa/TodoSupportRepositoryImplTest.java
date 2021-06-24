package com.nzzi.guide.todo.domain.todo.dao.jpa;

import com.nzzi.guide.todo.domain.todo.dto.TodoPredicate;
import com.nzzi.guide.todo.domain.todo.dto.TodoRequest;
import com.nzzi.guide.todo.domain.todo.model.Todo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TodoSupportRepositoryImplTest {

    @Autowired
    private TodoRepository todoRepository;

    @Test
    @DisplayName("Todo 정보를 데이터베이스에 정상적으로 저장한다.")
    @Transactional
    @Order(1)
    public void save_todo_success() {

        // given
        final Long expectedId;
        Todo expectedTodoEntity = dummyTodo();

        // when
        Todo returnedTodoEntity = todoRepository
                .save(expectedTodoEntity);
        expectedId = returnedTodoEntity.getIdx();
        Todo actualTodoEntity = todoRepository.getOne(expectedId);

        // then
        assertNotNull(actualTodoEntity.getIdx());
        assertNotNull(actualTodoEntity.getCreatedDate());
        assertNotNull(actualTodoEntity.getLastModifiedDate());
        assertTrue(actualTodoEntity.getIsActive());

        assertEquals(expectedId, actualTodoEntity.getIdx());
        assertEquals(expectedTodoEntity.getTitle(), actualTodoEntity.getTitle());
        assertEquals(expectedTodoEntity.getContents(), actualTodoEntity.getContents());

        assertEquals(expectedId, returnedTodoEntity.getIdx());
        assertEquals(expectedTodoEntity.getTitle(), returnedTodoEntity.getTitle());
        assertEquals(expectedTodoEntity.getContents(), returnedTodoEntity.getContents());
    }

    @Test
    @DisplayName("contents 필드를 통해 Todo 정보를 검색하여 정상적으로 가져온다.")
    @Transactional
    @Order(2)
    public void search_by_contents_success() {

        // given
        Todo expectedTodoEntity = dummyTodo();
        final Long expectedId = todoRepository.save(expectedTodoEntity)
                .getIdx();

        TodoPredicate todoPredicate = TodoPredicate.builder()
                .contents(expectedTodoEntity.getContents())
                .build();

        // when
        Page<Todo> actualSearchedPage = todoRepository
                .search(todoPredicate, defaultPageRequest());

        // then
        assertFalse(actualSearchedPage.isEmpty());
        assertFalse(actualSearchedPage.filter(todo -> todo.getIdx().equals(expectedId)).isEmpty());
    }

    private PageRequest defaultPageRequest() {
        final int defaultPage = 0;
        final int defaultSize = 10;
        final Sort.Direction defaultDirection = Sort.Direction.DESC;
        final String defaultProperty = "idx";
        return PageRequest.of(defaultPage, defaultSize,
                Sort.by(defaultDirection, defaultProperty));
    }

    private Todo dummyTodo() {
        final String dummyTitle = "타이틀";
        final String dummyContents = "테스트 콘텐츠";

        return TodoRequest.of(dummyTitle, dummyContents).toEntity();
    }
}
