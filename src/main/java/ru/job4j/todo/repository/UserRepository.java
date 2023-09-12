package ru.job4j.todo.repository;

import ru.job4j.todo.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    boolean save(User user);

    Collection<User> findAll();

    Optional<User> findById(int id);

    Optional<User> findByLoginAndPassword(String login, String password);

    boolean deleteById(int id);
}
