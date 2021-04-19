package com.nzzi.guide.todo.domain.todo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nzzi.guide.todo.domain.todo.model.Todo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TodoResponse implements Serializable {

    private boolean isActive;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastModifiedDate;

    private Long idx;
    private String title;
    private String contents;

    public static TodoResponse of(Todo entity) {
        if(entity == null)
            return null;
        return new ModelMapper()
                .map(entity, TodoResponse.class);
    }

    public static Page<TodoResponse> of(Page<Todo> entities) {
        if(entities == null)
            return Page.empty();
        return entities.map(TodoResponse::of);
    }
}