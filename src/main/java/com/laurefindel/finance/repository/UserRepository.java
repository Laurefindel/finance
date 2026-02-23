package com.laurefindel.finance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.laurefindel.finance.model.entity.User;


public interface UserRepository extends JpaRepository<User, Long> {
    
    @EntityGraph(attributePaths = {"roles", "accounts"})
    List<User> findByFirstNameAndLastName(String firstName, String lastName);

    @EntityGraph(attributePaths = {"roles", "accounts"})
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"roles", "accounts"})
    List<User> findByStatus(String status);

    @EntityGraph(attributePaths = {"roles", "accounts"})
    List<User> findByFirstName(String firstName);

    @EntityGraph(attributePaths = {"roles", "accounts"})
    List<User> findByLastName(String lastName);

    @EntityGraph(attributePaths = {"roles", "accounts"})
    List<User> findByRoles_Name(String role);   
}
