package com.github.egorovag.clevertest.clever.service;

import com.github.egorovag.clevertest.clever.entities.User;
import com.github.egorovag.clevertest.clever.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public User findByLoginOrCreate(String login) {
        User userLogin = findByLogin(login);
        if (isNull(userLogin)) {
            return create(login);
        }
        return userLogin;
    }

    private User findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    private User create(String login) {
        try {
            log.info("User by login: {} created", login);
            return userRepository.save(new User(login));
        } catch (HibernateException e) {
            log.error("Fail to create user by login: {}", login);
            return null;
        }
    }
}
