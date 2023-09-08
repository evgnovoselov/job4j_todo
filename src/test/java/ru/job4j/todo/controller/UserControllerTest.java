package ru.job4j.todo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.todo.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class UserControllerTest {
    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void whenGetRegistrationPageThenReturnViewWithUserObject() {
        ConcurrentModel model = new ConcurrentModel();
        String view = userController.getRegistrationPage(model);
        User actualUser = (User) model.getAttribute("user");

        assertThat(view).isEqualTo("users/registration");
        assertThat(actualUser).usingRecursiveComparison().isEqualTo(new User());
    }
}