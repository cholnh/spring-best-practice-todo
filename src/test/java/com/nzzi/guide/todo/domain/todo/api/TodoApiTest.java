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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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

    @Nested
    @DisplayName("Todo 반환 테스트")
    class findOne {
        @Test
        @WithMockUser(roles="USER")
        @DisplayName("USER 권한은 Todo 정보를 정상적으로 반환한다.")
        void findOne_success_by_user() throws Exception {

            // given
            final Todo savedTodo = todoRepository.save(TodoBuilder.mock());

            // when
            final ResultActions resultActions = requestFindOneTodo(savedTodo.getIdx());

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
        @DisplayName("OAuth 권한이 없으면 Todo 정보 반환이 거부된다.")
        void findOne_unauthorized() throws Exception {

            // given
            final Long dummyId = 1L;

            // when
            final ResultActions resultActions = requestFindOneTodo(dummyId);

            // then
            resultActions
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Todo 리스트 반환 테스트")
    class findAll {
        @Test
        @WithMockUser(roles="USER")
        @DisplayName("USER 권한은 Todo 리스트를 페이지화 하여 정상적으로 반환한다.")
        void findAll_success_by_user() throws Exception {

            // given
            final PageRequest page = PageRequestBuilder.build();
            final int mockEntityCount = 5;
            for (int i = 0; i < mockEntityCount; i++) {
                Todo todo = todoRepository.save(TodoBuilder.mock());
                System.out.println(todo.getIdx());
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
        @DisplayName("OAuth 권한이 없으면 Todo 정보 반환이 거부된다.")
        void findAll_unauthorized() throws Exception {

            // given
            final PageRequest page = PageRequestBuilder.build();

            // when
            final ResultActions resultActions = requestFindAllTodo(page);

            // then
            resultActions
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Todo 검색 테스트")
    class findGroup {
        @Test
        @WithMockUser(roles="USER")
        @DisplayName("USER 권한은 query 조건문(단일조건)에 일치하는 Todo 를 찾아 정상적으로 반환한다.")
        void findGroup_success_1() throws Exception {

            // given
            final Todo savedTodo = todoRepository.save(TodoBuilder.mock());
            final String query = "contents=" + savedTodo.getContents();
            final PageRequest page = PageRequestBuilder.build();

            // when
            final ResultActions resultActions = requestFindGroupTodo(query, page);

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
        @DisplayName("USER 권한은 query 조건문(복수조건)에 일치하는 Todo 를 찾아 정상적으로 반환한다.")
        void findGroup_success_2() throws Exception {

            // given
            final String savedTodoTitle = "오늘 할 일 목록";
            final String savedTodoContents = "책 읽기, 운동하기, 카페가기";
            final Todo savedTodo = todoRepository.save(TodoBuilder.build(savedTodoTitle, savedTodoContents));

            final String query = "title=오늘, contents=코딩";
            final PageRequest page = PageRequestBuilder.build();

            // when
            final ResultActions resultActions = requestFindGroupTodo(query, page);

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
        @DisplayName("OAuth 권한이 없으면 Todo 정보 검색이 거부된다.")
        void findGroup_unauthorized() throws Exception {

            // given
            final String dummyQuery = "";
            final PageRequest dummyPage = PageRequestBuilder.build();

            // when
            final ResultActions resultActions = requestFindGroupTodo(dummyQuery, dummyPage);

            // then
            resultActions
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Todo 생성 테스트")
    class create {
        @Test
        @WithMockUser(roles="USER")
        @DisplayName("USER 권한은 Todo 정보를 정상적으로 생성한다.")
        void create_success() throws Exception {

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
        @DisplayName("OAuth 권한이 없으면 Todo 정보 생성이 거부된다.")
        void create_unauthorized() throws Exception {

            // given
            final TodoRequest dto = TodoRequestBuilder.mock();

            // when
            final ResultActions resultActions = requestCreateTodo(dto);

            // then
            resultActions
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Todo 수정 테스트")
    class update {
        @Test
        @WithMockUser(roles="USER")
        @DisplayName("USER 권한은 id에 해당하는 Todo 정보를 정상적으로 수정한다.")
        void update_success() throws Exception {

            // given
            final Todo savedTodo = todoRepository.saveAndFlush(TodoBuilder.build("title", "contents"));
            final TodoRequest dto = TodoRequestBuilder.build("updated title", "updated contents");

            // when
            final ResultActions resultActions = requestUpdateTodo(savedTodo.getIdx(), dto);
            final Todo updatedTodo = todoRepository.getOne(savedTodo.getIdx());

            // then
            resultActions
                    .andExpect(status().isAccepted())
                    .andExpect(header().exists(HttpHeaders.LOCATION));
            assertEquals(dto.getTitle(), updatedTodo.getTitle());
            assertEquals(dto.getContents(), updatedTodo.getContents());
        }

        @Test
        @DisplayName("OAuth 권한이 없으면 Todo 정보 수정이 거부된다.")
        void update_unauthorized() throws Exception {

            // given
            final TodoRequest dto = TodoRequestBuilder.mock();

            // when
            final ResultActions resultActions = requestUpdateTodo(1L, dto);

            // then
            resultActions
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    @DisplayName("id에 해당하는 Todo 정보를 정상적으로 삭제한다.")
    void delete() {
    }

    private String dateFormat(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private ResultActions requestFindOneTodo(Long id) throws Exception {
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

    private ResultActions requestFindGroupTodo(String query, PageRequest page) throws Exception {
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
}