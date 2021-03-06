package com.nzzi.guide.todo.domain.todo.api;

import com.nzzi.guide.todo.domain.todo.dto.TodoPredicate;
import com.nzzi.guide.todo.domain.todo.dto.TodoRequest;
import com.nzzi.guide.todo.domain.todo.dto.TodoResponse;
import com.nzzi.guide.todo.domain.todo.service.TodoCommandService;
import com.nzzi.guide.todo.domain.todo.service.TodoQueryService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/todos")
@AllArgsConstructor
public class TodoApi {

    private TodoQueryService todoQueryService;
    private TodoCommandService todoCommandService;

    @GetMapping("/{todoId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_GUEST')")
    public ResponseEntity<?> findOne(
            @PathVariable(value = "todoId") Long todoId
    ) {
        EntityModel<TodoResponse> resources = EntityModel.of(
            todoQueryService.findTodo(todoId));

        resources.add(linkTo(methodOn(TodoApi.class).findOne(todoId)).withSelfRel());
        return ResponseEntity.ok(resources);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_GUEST')")
    public ResponseEntity<?> findGroup(
            @RequestParam(value = "query", required = false) String query,
            @PageableDefault(sort = {"idx"}, direction = Sort.Direction.DESC, page = 0, size = 10) Pageable pageable
    ) {
        CollectionModel<TodoResponse> resources = CollectionModel.of(query == null
            ? todoQueryService.findTodos(pageable)
            : todoQueryService.search(TodoPredicate.of(query), pageable));

        resources.add(linkTo(methodOn(TodoApi.class).findGroup(query, pageable)).withSelfRel());
        return ResponseEntity.ok(resources);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity create(
            @Valid @RequestBody TodoRequest request
    ) {
        TodoResponse created = todoCommandService.create(request);
        Link link = linkTo(methodOn(TodoApi.class).findOne(created.getIdx()))
                .withSelfRel();
        return ResponseEntity
                .created(link.toUri())
                .build();
    }

    @PutMapping("/{todoId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity update(
            @PathVariable(value = "todoId") Long todoId,
            @RequestBody TodoRequest request
    ) {
        TodoResponse updated = todoCommandService.update(todoId, request);
        Link link = linkTo(methodOn(TodoApi.class).findOne(updated.getIdx()))
                .withSelfRel();
        return ResponseEntity
                .ok()
                .location(link.toUri())
                .build();
    }

    @DeleteMapping("/{todoId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity delete(
            @PathVariable(value = "todoId") Long todoId
    ) {
        todoCommandService.delete(todoId);
        return ResponseEntity
                .noContent()
                .build();
    }
}
