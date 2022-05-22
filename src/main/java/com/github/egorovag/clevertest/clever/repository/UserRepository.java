package com.github.egorovag.clevertest.clever.repository;

import com.github.egorovag.clevertest.clever.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByLogin(String login);
}
