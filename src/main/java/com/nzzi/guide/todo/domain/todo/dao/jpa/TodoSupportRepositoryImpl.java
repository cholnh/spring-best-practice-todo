package com.nzzi.guide.todo.domain.todo.dao.jpa;

import com.nzzi.guide.todo.domain.todo.dto.TodoPredicate;
import com.nzzi.guide.todo.domain.todo.model.QTodo;
import com.nzzi.guide.todo.domain.todo.model.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public class TodoSupportRepositoryImpl extends QuerydslRepositorySupport implements TodoSupportRepository {

    public TodoSupportRepositoryImpl() {
        super(Todo.class);
    }

    @Override
    public Page<Todo> searchByContents(TodoPredicate predicate, Pageable pageable) {

        final QTodo qTodo = QTodo.todo;

        List<Todo> result =
                from(qTodo)
                .select(qTodo)
                .where(predicate.build()
                .and(qTodo.isActive.isTrue()))
                .fetch();

        return new PageImpl<>(result, pageable, result.size());
    }
}
