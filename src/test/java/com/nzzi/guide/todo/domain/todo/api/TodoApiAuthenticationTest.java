package com.nzzi.guide.todo.domain.todo.api;

import com.nzzi.guide.todo._base.IntegrationTest;
import com.nzzi.guide.todo.domain._bases.PageRequestBuilder;
import com.nzzi.guide.todo.domain.todo.dto.TodoRequest;
import com.nzzi.guide.todo.domain.todo.dto.TodoRequestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TodoApiAuthenticationTest extends IntegrationTest {

    @BeforeEach
    void setUp() {
        mockMvcSetUp();
    }

    private void mockMvcSetUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Nested
    @DisplayName("Todo 반환 테스트")
    class findById {
        @Test
        @DisplayName("OAuth 권한이 없으면 id에 해당하는 Todo 정보 반환이 거부된다.")
        void unauthorized_findOne_shouldFailWith401() throws Exception {
            requestFindOneTodo().andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username="test-todo-admin1", roles = "ADMIN")
        @DisplayName("Admin 권한은 id에 해당하는 Todo 정보를 정상적으로 반환한다.")
        void authorizedAdmin_findOne_shouldSucceedWith200() throws Exception {
            requestFindOneTodo().andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username="test-todo-user1", roles = "USER")
        @DisplayName("USER 권한은 id에 해당하는 Todo 정보를 정상적으로 반환한다.")
        void authorizedUser_findOne_shouldSucceedWith200() throws Exception {
            requestFindOneTodo().andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "GUEST")
        @DisplayName("Guest 권한은 id에 해당하는 Todo 정보를 정상적으로 반환한다.")
        void authorizedGuest_findOne_shouldSucceedWith200() throws Exception {
            requestFindOneTodo().andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("모든 Todo 리스트 반환 테스트")
    class findAll {
        @Test
        @DisplayName("OAuth 권한이 없으면 모든 Todo 정보 반환이 거부된다.")
        void unauthorized_findAll_shouldFailWith401() throws Exception {
            requestFindAllTodo().andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username="test-todo-admin1", roles = "ADMIN")
        @DisplayName("Admin 권한은 모든 Todo 리스트를 페이지화 하여 정상적으로 반환한다.")
        void authorizedAdmin_findAll_shouldSucceedWith200() throws Exception {
            requestFindAllTodo().andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username="test-todo-user1", roles = "USER")
        @DisplayName("USER 권한은 모든 Todo 리스트를 페이지화 하여 정상적으로 반환한다.")
        void authorizedUser_findAll_shouldSucceedWith200() throws Exception {
            requestFindAllTodo().andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "GUEST")
        @DisplayName("Guest 권한은 모든 Todo 리스트를 페이지화 하여 정상적으로 반환한다.")
        void authorizedGuest_findAll_shouldSucceedWith200() throws Exception {
            requestFindAllTodo().andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Todo 검색 테스트")
    class search {
        @Test
        @DisplayName("OAuth 권한이 없으면 Todo 검색이 거부된다.")
        void unauthorized_search_shouldFailWith401() throws Exception {
            requestSearchTodo().andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username="test-todo-admin1", roles = "ADMIN")
        @DisplayName("Admin 권한은 Todo 검색 결과를 정상적으로 반환한다.")
        void authorizedAdmin_search_shouldSucceedWith200() throws Exception {
            requestSearchTodo().andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username="test-todo-user1", roles = "USER")
        @DisplayName("USER 권한은 Todo 검색 결과를 정상적으로 반환한다.")
        void authorizedUser_search_shouldSucceedWith200() throws Exception {
            requestSearchTodo().andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "GUEST")
        @DisplayName("Guest 권한은 Todo 검색 결과를 정상적으로 반환한다.")
        void authorizedGuest_search_shouldSucceedWith200() throws Exception {
            requestSearchTodo().andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Todo 생성 테스트")
    class create {
        @Test
        @DisplayName("OAuth 권한이 없으면 Todo 정보 생성이 거부된다.")
        void unauthorized_create_shouldFailWith401() throws Exception {
            requestCreateTodo().andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username="test-todo-admin1", roles = "ADMIN")
        @DisplayName("Admin 권한은 Todo 정보를 정상적으로 생성한다.")
        void authorizedAdmin_create_shouldSucceedWith201() throws Exception {
            requestCreateTodo().andExpect(status().isCreated());
        }

        @Test
        @WithMockUser(username="test-todo-user1", roles = "USER")
        @DisplayName("USER 권한은 Todo 정보를 정상적으로 생성한다.")
        void authorizedUser_create_shouldSucceedWith201() throws Exception {
            requestCreateTodo().andExpect(status().isCreated());
        }

        @Test
        @WithMockUser(roles = "GUEST")
        @DisplayName("Guest 권한은 Todo 정보 생성이 거부된다.")
        void authorizedGuest_create_shouldFailWith403() throws Exception {
            requestCreateTodo().andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Todo 수정 테스트")
    class update {
        @Test
        @DisplayName("OAuth 권한이 없으면 Todo 정보 수정이 거부된다.")
        void unauthorized_update_shouldFailWith401() throws Exception {
            requestUpdateTodo().andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username="test-todo-admin1", roles = "ADMIN")
        @DisplayName("Admin 권한은 Todo 정보를 정상적으로 수정한다.")
        void authorizedAdmin_update_shouldSucceedWith200() throws Exception {
            requestUpdateTodo().andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username="test-todo-user1", roles = "USER")
        @DisplayName("USER 권한은 Todo 정보를 정상적으로 수정한다.")
        void authorizedUser_update_shouldSucceedWith200() throws Exception {
            requestUpdateTodo().andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "GUEST")
        @DisplayName("Guest 권한은 Todo 정보 수정이 거부된다.")
        void authorizedGuest_update_shouldFailWith403() throws Exception {
            requestUpdateTodo().andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Todo 삭제 테스트")
    class delete {
        @Test
        @DisplayName("OAuth 권한이 없으면 Todo 정보 삭제가 거부된다.")
        void unauthorized_delete_shouldFailWith401() throws Exception {
            requestDeleteTodo().andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username="test-todo-admin1", roles = "ADMIN")
        @DisplayName("Admin 권한은 Todo 정보를 정상적으로 삭제한다.")
        void authorizedAdmin_delete_shouldSucceedWith204() throws Exception {
            requestDeleteTodo().andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(username="test-todo-user1", roles = "USER")
        @DisplayName("USER 권한은 Todo 정보를 정상적으로 삭제한다.")
        void authorizedUser_delete_shouldSucceedWith204() throws Exception {
            requestDeleteTodo().andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(roles = "GUEST")
        @DisplayName("Guest 권한은 Todo 정보 삭제가 거부된다.")
        void authorizedGuest_delete_shouldFailWith403() throws Exception {
            requestDeleteTodo().andExpect(status().isForbidden());
        }
    }

    private ResultActions requestFindOneTodo() throws Exception {
        return  mvc.perform(get("/todos/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    private ResultActions requestFindAllTodo() throws Exception {
        final PageRequest dummyPage = PageRequestBuilder.build();
        return  mvc.perform(get("/todos")
                .param("size", String.valueOf(dummyPage.getPageSize()))
                .param("page", String.valueOf(dummyPage.getPageNumber()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    private ResultActions requestSearchTodo() throws Exception {
        final String dummyQuery = "title=오늘, contents=코딩";
        final PageRequest dummyPage = PageRequestBuilder.build();
        return  mvc.perform(get("/todos")
                .param("query", dummyQuery)
                .param("size", String.valueOf(dummyPage.getPageSize()))
                .param("page", String.valueOf(dummyPage.getPageNumber()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    private ResultActions requestCreateTodo() throws Exception {
        final TodoRequest dummyDto = TodoRequestBuilder.mock();
        return mvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dummyDto)))
                .andDo(print());
    }

    private ResultActions requestUpdateTodo() throws Exception {
        final TodoRequest dummyDto = TodoRequestBuilder.mock();
        return mvc.perform(put("/todos/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dummyDto)))
                .andDo(print());
    }

    private ResultActions requestDeleteTodo() throws Exception {
        return mvc.perform(delete("/todos/{id}", 1L))
                .andDo(print());
    }
}
