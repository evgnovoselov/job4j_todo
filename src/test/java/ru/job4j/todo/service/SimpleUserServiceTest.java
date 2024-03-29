package ru.job4j.todo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.todo.model.User;
import ru.job4j.todo.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.job4j.todo.util.UserUtil.makeUser;

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
        User user = new User();
        user.setTimezone("UTC");

        boolean isSave = userService.save(user);

        assertThat(isSave).isTrue();
    }

    @Test
    void whenSaveUserNotCorrectThenReturnFalse() {
        when(userRepository.save(any(User.class))).thenReturn(false);

        boolean isSave = userService.save(new User());

        assertThat(isSave).isFalse();
    }

    @Test
    void whenSaveUserNotValidateTimezoneReturnFalse() {
        when(userRepository.save(any(User.class))).thenReturn(true);
        User user = new User();
        user.setTimezone("Not_Valid_Timezone");

        boolean isSave = userService.save(user);

        assertThat(isSave).isFalse();
    }

    @Test
    void whenFindUserByLoginAndPasswordRepositoryHaveThenReturnOptionalUser() {
        User user = makeUser(7);
        when(userRepository.findByLoginAndPassword(anyString(), anyString())).thenReturn(Optional.of(user));

        User actualUser = userService.findByLoginAndPassword(user.getLogin(), user.getPassword()).orElseThrow();

        assertThat(actualUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    void whenFindUserByLoginAndPasswordRepositoryNotHaveThenReturnOptionalEmpty() {
        User user = makeUser(7);
        when(userRepository.findByLoginAndPassword(anyString(), anyString())).thenReturn(Optional.empty());

        Optional<User> actualUser = userService.findByLoginAndPassword(user.getLogin(), user.getPassword());

        assertThat(actualUser).usingRecursiveComparison().isEqualTo(Optional.empty());
    }
}