package com.nzzi.guide.todo.domain.todo.dao.jpa;

import com.nzzi.guide.todo.domain.todo.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = true)
public interface TodoRepository extends JpaRepository<Todo, Long>, TodoSupportRepository {
    List<Todo> searchByTitleContains(String query);
}