package com.nzzi.guide.todo.domain.todo.service;

import com.nzzi.guide.todo.domain.todo.dto.TodoPredicate;
import com.nzzi.guide.todo.domain.todo.dto.TodoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TodoQueryService {
    TodoResponse findTodo(Long id);
    Page<TodoResponse> findTodos(Pageable pageable);
    Page<TodoResponse> search(TodoPredicate predicate, Pageable pageable);
}