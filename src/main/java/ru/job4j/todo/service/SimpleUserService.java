package ru.job4j.todo.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.todo.model.User;
import ru.job4j.todo.repository.UserRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class SimpleUserService implements UserService {
    private final UserRepository userRepository;

    @Override
    public boolean save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByLoginAndPassword(String login, String password) {
        return userRepository.findByLoginAndPassword(login, password);
    }
}
