package ru.job4j.todo.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.job4j.todo.model.User;
import ru.job4j.todo.repository.UserRepository;
import ru.job4j.todo.util.TimeZoneUtil;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class SimpleUserService implements UserService {
    private final UserRepository userRepository;

    @Override
    public boolean save(User user) {
        try {
            validate(user);
            return userRepository.save(user);
        } catch (Exception e) {
            log.error("Ошибка валидации нового пользователя: {}", e.getMessage());
        }
        return false;
    }

    private void validate(User user) {
        if (!TimeZoneUtil.getTimeZonesForDisplay().containsKey(user.getTimezone())) {
            throw new IllegalArgumentException("Выбран неправильный часовой пояс.");
        }
    }

    @Override
    public Optional<User> findByLoginAndPassword(String login, String password) {
        return userRepository.findByLoginAndPassword(login, password);
    }
}
