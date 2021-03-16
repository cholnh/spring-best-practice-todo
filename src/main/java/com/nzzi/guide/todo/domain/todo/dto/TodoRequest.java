package com.nzzi.guide.todo.domain.todo.dto;

import com.nzzi.guide.todo.domain.todo.model.Todo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.Valid;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class TodoRequest {

    @Valid
    private String title;

    @Valid
    private String contents;

    public TodoRequest(@Valid String title, @Valid String contents) {
        this.title = title;
        this.contents = contents;
    }

    public Todo toEntity() {
        return Todo.builder()
                .title(title)
                .contents(contents)
                .build();
    }
}
