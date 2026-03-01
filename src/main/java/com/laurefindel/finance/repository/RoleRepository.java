package com.laurefindel.finance.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.laurefindel.finance.model.entity.Role;
import java.util.List;
import com.laurefindel.finance.model.entity.User;
import java.util.Set;


public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);

    @EntityGraph(attributePaths = "users")
    List<Role> findByUsers(Set<User> users);
}
