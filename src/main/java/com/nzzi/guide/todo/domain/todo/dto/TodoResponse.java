package com.nzzi.guide.todo.domain.todo.dto;

import com.nzzi.guide.todo.domain.todo.model.Todo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class TodoResponse implements Serializable {

    private boolean isActive;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    private Long idx;
    private String title;
    private String contents;

    public static TodoResponse of(Todo entity) {
        if(entity == null) return null;
        TodoResponse dto = new ModelMapper().map(entity, TodoResponse.class);
        return dto;
    }

    public static Page<TodoResponse> of(Page<Todo> entities) {
        if(entities == null) return Page.empty();
        return entities.map(TodoResponse::of);
    }
}