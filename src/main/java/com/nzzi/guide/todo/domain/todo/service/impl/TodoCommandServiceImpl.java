package com.nzzi.guide.todo.domain.todo.service.impl;

import com.nzzi.guide.todo.domain.todo.dao.jpa.TodoRepository;
import com.nzzi.guide.todo.domain.todo.dto.TodoRequest;
import com.nzzi.guide.todo.domain.todo.dto.TodoResponse;
import com.nzzi.guide.todo.domain.todo.exception.TodoNotFoundException;
import com.nzzi.guide.todo.domain.todo.model.Todo;
import com.nzzi.guide.todo.domain.todo.service.TodoCommandService;
import com.nzzi.guide.todo.global.configuration.mapper.CustomMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TodoCommandServiceImpl implements TodoCommandService {

    private TodoRepository todoRepository;

    @Override
    @Transactional
    public TodoResponse create(TodoRequest request) {
        return TodoResponse.of(todoRepository.save(request.toEntity()));
    }

    @Override
    @Transactional
    public TodoResponse update(Long id, TodoRequest request) {
        Todo target = findById(id);
        CustomMapper.getInstance()
                .map(request.toEntity(), target);

        return TodoResponse.of(todoRepository.save(target));
    }

    @Override
    @Transactional
    public TodoResponse delete(Long id) {
        Todo target = findById(id);
        todoRepository.delete(target);

        return TodoResponse.of(target);
    }

    private Todo findById(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id.toString()));
    }
}
