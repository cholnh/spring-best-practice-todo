package com.nzzi.guide.todo.domain.todo.service.impl;

import com.nzzi.guide.todo.domain.todo.dao.jpa.TodoRepository;
import com.nzzi.guide.todo.domain.todo.dto.TodoPredicate;
import com.nzzi.guide.todo.domain.todo.dto.TodoResponse;
import com.nzzi.guide.todo.domain.todo.exception.TodoNotFoundException;
import com.nzzi.guide.todo.domain.todo.model.Todo;
import com.nzzi.guide.todo.domain.todo.service.TodoQueryService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TodoQueryServiceImpl implements TodoQueryService {

    private TodoRepository todoRepository;

    @Override
    public TodoResponse findTodo(Long id) {
        Todo todo = findById(id);
        return TodoResponse.of(todo);
    }

    @Override
    public Page<TodoResponse> findTodos(Pageable pageable) {
        Page<Todo> todos = todoRepository.findAll(pageable);
        return todos.map(TodoResponse::of);
    }

    @Override
    public Page<TodoResponse> searchByContents(TodoPredicate predicate, Pageable pageable) {
        Page<Todo> todos = todoRepository.searchByContents(predicate, pageable);
        return todos.map(TodoResponse::of);
    }

    private Todo findById(Long id) {
        if (id == null || id <= 0)
            throw new TodoNotFoundException();
        return todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id.toString()));
    }
}
