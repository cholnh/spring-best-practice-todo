package com.nzzi.guide.todo.domain.user.infrastructure.jpa;

import com.nzzi.guide.todo.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface UserRepository extends JpaRepository<User, Long>, UserSupportRepository {
}
