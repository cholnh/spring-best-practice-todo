package com.nzzi.guide.todo.domain.user.infrastructure.jpa;

import com.nzzi.guide.todo.domain.user.model.QUser;
import com.nzzi.guide.todo.domain.user.model.User;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public class UserSupportRepositoryImpl extends QuerydslRepositorySupport implements UserSupportRepository {

    public UserSupportRepositoryImpl() {
        super(User.class);
    }

    @Override
    public Optional<User> findByUsername(String username) {

        final QUser qUser = QUser.user;

        return Optional.of(from(qUser)
                .select(qUser)
                .where(qUser.username.eq(username)
                .and(qUser.isActive.isTrue()))
                .fetchOne());
    }
}
