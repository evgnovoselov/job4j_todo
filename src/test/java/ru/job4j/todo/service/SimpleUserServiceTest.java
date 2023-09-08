package ru.job4j.todo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.todo.model.User;
import ru.job4j.todo.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimpleUserServiceTest {
    private SimpleUserService userService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new SimpleUserService(userRepository);
    }

    @Test
    void whenSaveUserCorrectReturnTrue() {
        when(userRepository.save(any(User.class))).thenReturn(true);

        boolean isSave = userService.save(new User());

        assertThat(isSave).isTrue();
    }

    @Test
    void whenSaveUserNotCorrectThenReturnFalse() {
        when(userRepository.save(any(User.class))).thenReturn(false);

        boolean isSave = userService.save(new User());

        assertThat(isSave).isFalse();
    }
}