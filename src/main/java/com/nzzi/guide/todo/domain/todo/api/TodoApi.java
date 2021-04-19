package com.nzzi.guide.todo.domain.todo.api;

import com.nzzi.guide.todo.domain.todo.service.TodoFindService;
import com.nzzi.guide.todo.domain.todo.dto.TodoPredicate;
import com.nzzi.guide.todo.domain.todo.dto.TodoResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/todos")
@AllArgsConstructor
public class TodoApi {

    private TodoFindService todoFindService;

    @GetMapping("/{todoId}")
    public ResponseEntity<?> findOne(@PathVariable(value = "todoId", required = true) Long todoId) {

        EntityModel<TodoResponse> resources = EntityModel.of(
            todoFindService.findTodo(todoId));

        resources.add(linkTo(methodOn(TodoApi.class).findOne(todoId)).withSelfRel());
        return ResponseEntity.ok(resources);
    }

    @GetMapping
    public ResponseEntity<?> findGroup(
            @RequestParam(value = "query", required = false) String query,
            @PageableDefault(sort = {"idx"}, direction = Sort.Direction.DESC, page = 0, size = 10) Pageable pageable
    ) {
        CollectionModel<TodoResponse> resources = CollectionModel.of(query == null
            ? todoFindService.findTodos(pageable)
            : todoFindService.searchByContents(TodoPredicate.of(query), pageable));

        resources.add(linkTo(methodOn(TodoApi.class).findGroup(query, pageable)).withSelfRel());
        return ResponseEntity.ok(resources);
    }
}
