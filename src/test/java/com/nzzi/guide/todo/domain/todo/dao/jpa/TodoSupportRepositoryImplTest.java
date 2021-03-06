package com.nzzi.guide.todo.domain.todo.dao.jpa;

import com.nzzi.guide.todo._base.RepositoryTest;
import com.nzzi.guide.todo.domain._bases.PageRequestBuilder;
import com.nzzi.guide.todo.domain.todo.dto.TodoPredicate;
import com.nzzi.guide.todo.domain.todo.dto.TodoRequest;
import com.nzzi.guide.todo.domain.todo.model.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

public class TodoSupportRepositoryImplTest extends RepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    private Todo savedTodo;

    @BeforeEach
    void setUp() {
        final String dummyTitle = "타이틀";
        final String dummyContents = "테스트 콘텐츠";

        Todo todo = TodoRequest.of(dummyTitle, dummyContents).toEntity();
        savedTodo = todoRepository.save(todo);
    }

    @Test
    @DisplayName("id에 해당하는 Todo 정보를 정상적으로 가져온다.")
    @Transactional
    @Order(1)
    public void find_byId_returnsTodo() {

        // given
        final Long expectedId = savedTodo.getIdx();

        // when
        Todo actualTodoEntity = todoRepository.getOne(expectedId);

        // then
        assertNotNull(actualTodoEntity.getIdx());
        assertNotNull(actualTodoEntity.getCreatedDate());
        assertNotNull(actualTodoEntity.getLastModifiedDate());
        assertTrue(actualTodoEntity.getIsActive());

        assertEquals(expectedId, actualTodoEntity.getIdx());
        assertEquals(savedTodo.getTitle(), actualTodoEntity.getTitle());
        assertEquals(savedTodo.getContents(), actualTodoEntity.getContents());
    }

    @Test
    @DisplayName("title 필드를 통해 Todo 정보를 검색하여 정상적으로 가져온다.")
    @Transactional
    @Order(2)
    public void search_byTitle_returnsPageOfTodo() {

        // given
        final Long expectedId = savedTodo.getIdx();
        final String expectedTitle = savedTodo.getTitle();

        TodoPredicate todoPredicate = TodoPredicate.builder()
                .title(expectedTitle)
                .build();

        // when
        Page<Todo> actualSearchedPage = todoRepository
                .search(todoPredicate, PageRequestBuilder.build());

        // then
        assertFalse(actualSearchedPage.isEmpty());
        assertFalse(actualSearchedPage.filter(
                todo -> todo.getIdx().equals(expectedId) &&
                        todo.getTitle().equals(expectedTitle)
        ).isEmpty());
    }

    @Test
    @DisplayName("contents 필드를 통해 Todo 정보를 검색하여 정상적으로 가져온다.")
    @Transactional
    @Order(3)
    public void search_byContents_returnsPageOfTodo() {

        // given
        final Long expectedId = savedTodo.getIdx();
        final String expectedContents = savedTodo.getContents();

        TodoPredicate todoPredicate = TodoPredicate.builder()
                .contents(expectedContents)
                .build();

        // when
        Page<Todo> actualSearchedPage = todoRepository
                .search(todoPredicate, PageRequestBuilder.build());

        // then
        assertFalse(actualSearchedPage.isEmpty());
        assertFalse(actualSearchedPage.filter(
                todo -> todo.getIdx().equals(expectedId) &&
                        todo.getContents().equals(expectedContents)
        ).isEmpty());
    }
}
