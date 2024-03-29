package ru.job4j.todo.controller;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static ru.job4j.todo.util.UserUtil.makeUser;

class UserControllerTest {
    private UserController userController;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    void whenGetRegistrationPageThenReturnViewWithUserObject() {
        ConcurrentModel model = new ConcurrentModel();
        String view = userController.getRegistrationPage(model);
        User actualUser = (User) model.getAttribute("user");

        assertThat(view).isEqualTo("users/registration");
        assertThat(actualUser).usingRecursiveComparison().isEqualTo(new User());
    }

    @Test
    void whenRegistrationUserSuccessThenRedirectIndexPageWithUserInSession() {
        User user = makeUser(7);
        when(userService.save(any(User.class))).thenReturn(true);
        ConcurrentModel model = new ConcurrentModel();
        MockHttpSession session = new MockHttpSession();

        String view = userController.processRegistration(user, model, session);
        User actualUserSession = (User) session.getAttribute("user");

        assertThat(view).isEqualTo("redirect:/");
        assertThat(actualUserSession).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    void whenRegistrationUserNotSaveThenGetPageRegistrationUserWithMessage() {
        User user = makeUser(7);
        when(userService.save(any(User.class))).thenReturn(false);
        ConcurrentModel model = new ConcurrentModel();
        MockHttpSession session = new MockHttpSession();

        String view = userController.processRegistration(user, model, session);
        String error = (String) model.getAttribute("error");
        User actualUser = (User) model.getAttribute("user");

        assertThat(view).isEqualTo("users/registration");
        assertThat(error).isEqualTo("Пользователь с таким логином уже существует или указан неверный часовой пояс.");
        assertThat(actualUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    void whenGetLoginPageThenReturnViewWithUser() {
        ConcurrentModel model = new ConcurrentModel();
        String view = userController.login(model);
        User actualUser = (User) model.getAttribute("user");

        assertThat(view).isEqualTo("users/login");
        assertThat(actualUser).usingRecursiveComparison().isEqualTo(new User());
    }

    @Test
    void whenLoginUserCorrectThenRedirectIndexPageWithUserInSession() {
        User user = makeUser(7);
        when(userService.findByLoginAndPassword(user.getLogin(), user.getPassword())).thenReturn(Optional.of(user));

        ConcurrentModel model = new ConcurrentModel();
        MockHttpSession session = new MockHttpSession();
        String view = userController.processLogin(user, model, session);
        User actualUser = (User) session.getAttribute("user");

        assertThat(view).isEqualTo("redirect:/");
        assertThat(actualUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    void whenLoginUserNotCorrectThenGetLoginPageWithErrorMessage() {
        when(userService.findByLoginAndPassword(anyString(), anyString())).thenReturn(Optional.empty());

        ConcurrentModel model = new ConcurrentModel();
        MockHttpSession session = new MockHttpSession();
        String view = userController.processLogin(new User(), model, session);
        String error = (String) model.getAttribute("error");

        assertThat(view).isEqualTo("users/login");
        assertThat(error).isEqualTo("Логин или пароль введены неверно.");
    }

    @Test
    void whenProcessLogoutThenRedirectLoginPageAndInvalidateSession() {
        HttpSession session = mock(HttpSession.class);

        String view = userController.processLogout(session);

        verify(session).invalidate();
        assertThat(view).isEqualTo("redirect:/users/login");
    }
}