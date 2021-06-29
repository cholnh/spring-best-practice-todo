package com.nzzi.guide.todo.domain.todo.dto;

public class TodoRequestBuilder {

    public static TodoRequest mock() {

        final String title = "타이틀";
        final String contents = "테스트 콘텐츠";

        return TodoRequest.of(title, contents);
    }
}
