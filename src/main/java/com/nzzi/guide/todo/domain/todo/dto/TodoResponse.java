package com.nzzi.guide.todo.domain.todo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nzzi.guide.todo.domain.todo.model.Todo;
import com.nzzi.guide.todo.global.configuration.mapper.CustomMapper;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
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
        return CustomMapper.getInstance()
                .map(entity, TodoResponse.class);
    }
}