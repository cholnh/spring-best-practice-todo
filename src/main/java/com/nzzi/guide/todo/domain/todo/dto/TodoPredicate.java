package com.nzzi.guide.todo.domain.todo.dto;

import com.nzzi.guide.todo.domain.todo.model.QTodo;
import com.querydsl.core.BooleanBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Arrays;

@Getter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TodoPredicate {

    private String title;
    private String contents;

    public BooleanBuilder build() {
        final QTodo qtodo = QTodo.todo;
        final BooleanBuilder builder = new BooleanBuilder();

        if (this.title != null)
            builder.and(qtodo.title.containsIgnoreCase(this.title));
        if (this.contents != null)
            builder.and(qtodo.contents.containsIgnoreCase(this.contents));

        return builder;
    }

    public static TodoPredicate of(String queryString) {
        final String DELIMITER_QUERY = ",";
        final String DELIMITER_KEY_VALUE = "=";
        final TodoPredicate todoPredicate = new TodoPredicate();

        if (queryString == null)
            return todoPredicate;

        Arrays.asList(queryString.split(DELIMITER_QUERY))
                .forEach((queryKeyValue) -> {
            String key = queryKeyValue.split(DELIMITER_KEY_VALUE)[0];
            String value = queryKeyValue.split(DELIMITER_KEY_VALUE)[0];
            mapping(todoPredicate, key, value);
        });

        return todoPredicate;
    }

    private static void mapping(TodoPredicate predicate, String key, String value) {
        switch (key) {
            case "title":
                predicate.title = value; break;
            case "contents":
                predicate.contents = value; break;
            default: break;
        }
    }
}
