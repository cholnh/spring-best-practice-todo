package com.nzzi.guide.todo.domain._bases;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageRequestBuilder {

    public static PageRequest build() {
        final int defaultPage = 0;
        final int defaultSize = 10;
        final Sort.Direction defaultDirection = Sort.Direction.DESC;
        final String defaultProperty = "idx";
        return PageRequest.of(defaultPage, defaultSize,
                Sort.by(defaultDirection, defaultProperty));
    }
}
