package com.nzzi.guide.todo.domain.todo.service;

import com.nzzi.guide.todo.domain.todo.dto.TodoRequest;
import com.nzzi.guide.todo.domain.todo.dto.TodoResponse;

public interface TodoCommandService {
    TodoResponse create(TodoRequest request);
    TodoResponse update(Long id, TodoRequest request);
    TodoResponse delete(Long id);
}
