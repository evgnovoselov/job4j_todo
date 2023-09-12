package ru.job4j.todo.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.UserService;

import java.util.Optional;

@Controller
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/registration")
    public String getRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "users/registration";
    }

    @PostMapping("/registration")
    public String processRegistration(User user, Model model, HttpSession session) {
        boolean isSave = userService.save(user);
        if (!isSave) {
            model.addAttribute("error", "Пользователь с таким логином уже существует.");
            model.addAttribute("user", user);
            return "users/registration";
        }
        session.setAttribute("user", user);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new User());
        return "users/login";
    }

    @PostMapping("/login")
    public String processLogin(User user, Model model, HttpSession session) {
        Optional<User> userOptional = userService.findByLoginAndPassword(user.getLogin(), user.getPassword());
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "Логин или пароль введены неверно.");
            return "users/login";
        }
        session.setAttribute("user", userOptional.get());
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String processLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/users/login";
    }
}
