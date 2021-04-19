package com.nzzi.guide.todo.domain.todo.dto;

import com.nzzi.guide.todo.domain.todo.model.Todo;
import com.nzzi.guide.todo.global.configuration.mapper.CustomMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TodoRequest {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String contents;

    public Todo toEntity() {
        return CustomMapper.getInstance()
                .map(this, Todo.class);
    }
}
