package com.nzzi.guide.todo.domain.todo.service;

import com.nzzi.guide.todo.domain.todo.dto.TodoRequest;
import com.nzzi.guide.todo.domain.todo.model.Todo;

public interface TodoCommandService {
    Todo create(TodoRequest request);
    Todo update(Long id, TodoRequest request);
    Todo delete(Long id);
}
