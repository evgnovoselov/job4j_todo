package ru.job4j.todo.util;

import ru.job4j.todo.model.User;

public final class UserUtil {
    public static User makeUser(int seed) {
        User user = new User();
        user.setId(seed);
        user.setName("Name " + seed);
        user.setLogin("login" + seed);
        user.setPassword("password" + seed);
        return user;
    }
}
