package com.nzzi.guide.todo.domain.todo.model;

import java.time.LocalDateTime;

public class TodoBuilder {

    public static Todo mock() {

        final String title = "타이틀";
        final String contents = "테스트 콘텐츠";

        return build(null, title, contents);
    }

    public static Todo build(String title, String contents) {
        return build(null, title, contents);
    }

    public static Todo build(Long id, String title, String contents) {
        return Todo.builder()
                .isActive(true)
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .idx(id)
                .title(title)
                .contents(contents)
                .build();
    }
}
