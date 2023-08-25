package ru.job4j.todo.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.service.TaskService;

import java.util.Optional;

@Controller
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping()
    public String getAll(Model model) {
        model.addAttribute("tasks", taskService.findAllByOrderByCreatedDesc());
        return "tasks/list";
    }

    @GetMapping("/new")
    public String getAllNew(Model model) {
        model.addAttribute("tasks", taskService.findAllByDoneFalseOrderByCreatedDesc());
        return "tasks/list";
    }

    @GetMapping("/done")
    public String getAllDone(Model model) {
        model.addAttribute("tasks", taskService.findAllByDoneTrueOrderByCreatedDesc());
        return "tasks/list";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable int id, Model model) {
        Optional<Task> taskOptional = taskService.findById(id);
        if (taskOptional.isEmpty()) {
            model.addAttribute("message", "Задача не найдена.");
            return "error/404";
        }
        model.addAttribute("task", taskOptional.get());
        return "tasks/one";
    }
}
