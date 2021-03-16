package com.nzzi.guide.todo.domain.user.infrastructure.jpa;

import com.nzzi.guide.todo.domain.user.model.User;
import java.util.Optional;

public interface UserSupportRepository {
    Optional<User> findByUsername(String username);
}
