package com.nzzi.guide.todo.domain.todo.api;

import com.nzzi.guide.todo._base.IntegrationTest;
import com.nzzi.guide.todo.domain.todo.dao.jpa.TodoRepository;
import com.nzzi.guide.todo.domain.todo.dto.TodoRequest;
import com.nzzi.guide.todo.domain.todo.model.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TodoApiTest extends IntegrationTest {

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
    @WithMockUser(roles="USER")
    @DisplayName("Todo 정보를 정상적으로 반환한다.")
    void findOne() throws Exception {

        // given
        final Todo todo = savedTodo;

        // when
        ResultActions resultActions = mvc.perform(get("/todos/{id}", todo.getIdx())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("createdDate").value(dateFormat(todo.getCreatedDate())))
                .andExpect(jsonPath("lastModifiedDate").value(dateFormat(todo.getLastModifiedDate())))
                .andExpect(jsonPath("active").value(todo.getIsActive()))
                .andExpect(jsonPath("idx").value(todo.getIdx()))
                .andExpect(jsonPath("title").value(todo.getTitle()))
                .andExpect(jsonPath("contents").value(todo.getContents()));
    }

    @Test
    @DisplayName("query에 해당하는 Todo 를 찾아 정상적으로 반환한다.")
    void findGroup() {
    }

    @Test
    @DisplayName("Todo 정보를 정상적으로 생성한다.")
    void create() {
    }

    @Test
    @DisplayName("id에 해당하는 Todo 정보를 정상적으로 수정한다.")
    void update() {
    }

    @Test
    @DisplayName("id에 해당하는 Todo 정보를 정상적으로 삭제한다.")
    void delete() {
    }

    private String dateFormat(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}