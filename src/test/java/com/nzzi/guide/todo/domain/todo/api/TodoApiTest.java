package com.nzzi.guide.todo.domain.todo.api;

import com.nzzi.guide.todo._base.IntegrationTest;
import com.nzzi.guide.todo.domain._bases.PageRequestBuilder;
import com.nzzi.guide.todo.domain.todo.dao.jpa.TodoRepository;
import com.nzzi.guide.todo.domain.todo.dto.TodoRequest;
import com.nzzi.guide.todo.domain.todo.dto.TodoRequestBuilder;
import com.nzzi.guide.todo.domain.todo.model.Todo;
import com.nzzi.guide.todo.domain.todo.model.TodoBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TodoApiTest extends IntegrationTest {

    @Autowired
    private TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        todoRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles="USER")
    @DisplayName("id에 해당하는 Todo 정보를 정상적으로 반환한다.")
    void find_byId_returnsJsonContainingTodoResponse() throws Exception {

        // given
        final Todo savedTodo = todoRepository.save(TodoBuilder.mock());

        // when
        final ResultActions resultActions = requestFindByIdTodo(savedTodo.getIdx());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("createdDate").value(dateFormat(savedTodo.getCreatedDate())))
                .andExpect(jsonPath("lastModifiedDate").value(dateFormat(savedTodo.getLastModifiedDate())))
                .andExpect(jsonPath("active").value(savedTodo.getIsActive()))
                .andExpect(jsonPath("idx").value(savedTodo.getIdx()))
                .andExpect(jsonPath("title").value(savedTodo.getTitle()))
                .andExpect(jsonPath("contents").value(savedTodo.getContents()));
    }

    @Test
    @WithMockUser(roles="USER")
    @DisplayName("모든 Todo 리스트를 페이지화 하여 정상적으로 반환한다.")
    void findAll_withPageRequest_returnsJsonContainingPageOfTodoResponse() throws Exception {

        // given
        final PageRequest page = PageRequestBuilder.build();
        final int mockEntityCount = 5;
        for (int i = 0; i < mockEntityCount; i++) {
            todoRepository.save(TodoBuilder.mock());
        }

        // when
        final ResultActions resultActions = requestFindAllTodo(page);

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.todoResponses").exists())
                .andExpect(jsonPath("$._embedded.todoResponses").isArray())
                .andExpect(jsonPath("$._embedded.todoResponses", hasSize(mockEntityCount)));
    }

    @Test
    @WithMockUser(roles="USER")
    @DisplayName("query 조건문(단일조건)에 일치하는 Todo 를 찾아 정상적으로 반환한다.")
    void search_withSingleQueryAsParam_returnsJsonContainingPageOfTodoResponse() throws Exception {

        // given
        final Todo savedTodo = todoRepository.save(TodoBuilder.mock());
        final String query = "contents=" + savedTodo.getContents();
        final PageRequest page = PageRequestBuilder.build();

        // when
        final ResultActions resultActions = requestSearchTodo(query, page);

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.todoResponses").exists())
                .andExpect(jsonPath("$._embedded.todoResponses[0].createdDate").value(dateFormat(savedTodo.getCreatedDate())))
                .andExpect(jsonPath("$._embedded.todoResponses[0].lastModifiedDate").value(dateFormat(savedTodo.getLastModifiedDate())))
                .andExpect(jsonPath("$._embedded.todoResponses[0].active").value(savedTodo.getIsActive()))
                .andExpect(jsonPath("$._embedded.todoResponses[0].idx").value(savedTodo.getIdx()))
                .andExpect(jsonPath("$._embedded.todoResponses[0].title").value(savedTodo.getTitle()))
                .andExpect(jsonPath("$._embedded.todoResponses[0].contents").value(savedTodo.getContents()))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles="USER")
    @DisplayName("query 조건문(복수조건)에 일치하는 Todo 를 찾아 정상적으로 반환한다.")
    void search_withMultipleQueryAsParam_returnsJsonContainingPageOfTodoResponse() throws Exception {

        // given
        final Todo expectedTodo1 = todoRepository.save(TodoBuilder.build(
                "오늘 할 일",
                "책 읽기"));
        final Todo expectedTodo2 = todoRepository.save(TodoBuilder.build(
                "내일 할 일 목록",
                "코딩하기"));
        final Todo unexpectedTodo = todoRepository.save(TodoBuilder.build(
                "모레 할 일",
                "쇼핑하기"));

        final String query = "title=오늘, contents=코딩";
        final PageRequest page = PageRequest.of(0, 10,
                Sort.by(Sort.Direction.ASC, "idx"));

        // when
        final ResultActions resultActions = requestSearchTodo(query, page);


        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.todoResponses").exists())
                .andExpect(jsonPath("$._embedded.todoResponses", hasSize(2)))
                .andExpect(jsonPath("$._embedded.todoResponses[0].idx").value(expectedTodo1.getIdx()))
                .andExpect(jsonPath("$._embedded.todoResponses[1].idx").value(expectedTodo2.getIdx()))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles="USER")
    @DisplayName("todoRequest 입력을 받아 Todo 정보를 정상적으로 생성한다.")
    void create_simpleTodoRequest_created() throws Exception {

        // given
        final TodoRequest dto = TodoRequestBuilder.mock();

        // when
        final ResultActions resultActions = requestCreateTodo(dto);

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION));
    }

    @Test
    @WithMockUser(roles="USER")
    @DisplayName("todoRequest 입력을 받아 id에 해당하는 Todo 정보를 정상적으로 수정한다.")
    void update_byIdWithSimpleTodoRequest_updated() throws Exception {

        // given
        final Todo savedTodo = todoRepository.save(TodoBuilder.build("title", "contents"));
        final TodoRequest dto = TodoRequestBuilder.build("title to be updated", "contents to be updated");

        // when
        final ResultActions resultActions = requestUpdateTodo(savedTodo.getIdx(), dto);

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.LOCATION));

        final Optional<Todo> optional = todoRepository.findById(savedTodo.getIdx());
        assertTrue(optional.isPresent());
        final Todo updatedTodo = optional.get();
        assertEquals(dto.getTitle(), updatedTodo.getTitle());
        assertEquals(dto.getContents(), updatedTodo.getContents());
    }

    @Test
    @WithMockUser(roles="USER")
    @DisplayName("id에 해당하는 Todo 정보를 정상적으로 삭제한다.")
    void delete_byId_deleted() throws Exception {

        // given
        final Todo savedTodo = todoRepository.save(TodoBuilder.build("title to be deleted", "contents to be deleted"));

        // when
        final ResultActions resultActions = requestDeleteTodo(savedTodo.getIdx());

        // then
        resultActions
                .andExpect(status().isNoContent());

        final Optional<Todo> optional = todoRepository.findById(savedTodo.getIdx());
        assertFalse(optional.isPresent());
    }

    private String dateFormat(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private ResultActions requestFindByIdTodo(Long id) throws Exception {
        return  mvc.perform(get("/todos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    private ResultActions requestFindAllTodo(PageRequest page) throws Exception {
        return  mvc.perform(get("/todos")
                .param("size", String.valueOf(page.getPageSize()))
                .param("page", String.valueOf(page.getPageNumber()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    private ResultActions requestSearchTodo(String query, PageRequest page) throws Exception {
        return  mvc.perform(get("/todos")
                .param("query", query)
                .param("size", String.valueOf(page.getPageSize()))
                .param("page", String.valueOf(page.getPageNumber()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    private ResultActions requestCreateTodo(TodoRequest dto) throws Exception {
        return mvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print());
    }

    private ResultActions requestUpdateTodo(Long id, TodoRequest dto) throws Exception {
        return mvc.perform(put("/todos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print());
    }

    private ResultActions requestDeleteTodo(Long id) throws Exception {
        return mvc.perform(delete("/todos/{id}", id))
                .andDo(print());
    }
}