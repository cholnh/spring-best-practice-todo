package com.nzzi.guide.todo.domain.todo.service;

import com.nzzi.guide.todo.domain.todo.dto.TodoPredicate;
import com.nzzi.guide.todo.domain.todo.dto.TodoResponse;
import com.nzzi.guide.todo.domain.todo.exception.TodoNotFoundException;
import com.nzzi.guide.todo.domain.todo.dao.jpa.TodoRepository;
import com.nzzi.guide.todo.domain.todo.model.Todo;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TodoFindService {

    private TodoRepository todoRepository;

    public TodoResponse findTodo(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException("todo"));
        return TodoResponse.of(todo);
    }

    public Page<TodoResponse> findTodos(Pageable pageable) {
        Page<Todo> todos = todoRepository.findAll(pageable);
        return TodoResponse.of(todos);
    }

    public Page<TodoResponse> searchByContents(TodoPredicate predicate, Pageable pageable) {
        Page<Todo> todos = todoRepository.searchByContents(predicate, pageable);
        return TodoResponse.of(todos);
    }
}