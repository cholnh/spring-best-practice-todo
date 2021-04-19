package com.nzzi.guide.todo.domain.todo.dao.jpa;

import com.nzzi.guide.todo.domain.todo.dto.TodoPredicate;
import com.nzzi.guide.todo.domain.todo.model.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TodoSupportRepository {
    Page<Todo> searchByContents(TodoPredicate predicate, Pageable pageable);
}
