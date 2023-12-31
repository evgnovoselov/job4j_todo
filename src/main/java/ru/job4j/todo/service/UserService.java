package ru.job4j.todo.service;

import ru.job4j.todo.model.User;

import java.util.Optional;

public interface UserService {
    boolean save(User user);

    Optional<User> findByLoginAndPassword(String login, String password);
}
