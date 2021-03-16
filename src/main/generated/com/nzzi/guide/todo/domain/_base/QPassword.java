package com.nzzi.guide.todo.domain._base;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QPassword is a Querydsl query type for Password
 */
@Generated("com.querydsl.codegen.EmbeddableSerializer")
public class QPassword extends BeanPath<Password> {

    private static final long serialVersionUID = -1801448685L;

    public static final QPassword password = new QPassword("password");

    public final NumberPath<Integer> failedCount = createNumber("failedCount", Integer.class);

    public final StringPath passwordValue = createString("passwordValue");

    public QPassword(String variable) {
        super(Password.class, forVariable(variable));
    }

    public QPassword(Path<? extends Password> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPassword(PathMetadata metadata) {
        super(Password.class, metadata);
    }

}

