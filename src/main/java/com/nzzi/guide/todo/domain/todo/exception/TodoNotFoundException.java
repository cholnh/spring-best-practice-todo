package com.nzzi.guide.todo.domain.todo.exception;

import com.nzzi.guide.todo.global.error.exception.EntityNotFoundException;

public class TodoNotFoundException extends EntityNotFoundException {

    public TodoNotFoundException() {
        super("not found");
    }

    public TodoNotFoundException(String target) {
        super(target + " is not found");
    }
}
