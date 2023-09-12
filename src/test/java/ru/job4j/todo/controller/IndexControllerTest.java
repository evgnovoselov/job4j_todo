package ru.job4j.todo.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IndexControllerTest {
    private IndexController indexController;

    @BeforeEach
    void setUp() {
        indexController = new IndexController();
    }

    @Test
    void whenGetIndexPageThenRedirectToTasks() {
        String view = indexController.index();

        Assertions.assertThat(view).isEqualTo("index");
    }
}