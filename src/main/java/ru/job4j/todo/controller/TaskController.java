package ru.job4j.todo.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.todo.dto.TaskCreateDto;
import ru.job4j.todo.dto.TaskUpdateDto;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.CategoryService;
import ru.job4j.todo.service.PriorityService;
import ru.job4j.todo.service.TaskService;

import java.util.Optional;

@Controller
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final PriorityService priorityService;
    private final CategoryService categoryService;

    @GetMapping()
    public String getAll(Model model) {
        model.addAttribute("tasks", taskService.findAllByOrderByCreatedDesc());
        return "tasks/list";
    }

    @GetMapping("/new")
    public String getAllNew(Model model) {
        model.addAttribute("tasks", taskService.findAllByDoneOrderByCreatedDesc(false));
        return "tasks/list";
    }

    @GetMapping("/done")
    public String getAllDone(Model model) {
        model.addAttribute("tasks", taskService.findAllByDoneOrderByCreatedDesc(true));
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

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("task", taskService.getEmptyTaskCreateDto());
        model.addAttribute("priorities", priorityService.findAllByOrderByPosition());
        model.addAttribute("categories", categoryService.findAll());
        return "tasks/create";
    }

    @PostMapping("/create")
    public String processCreate(TaskCreateDto task, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Optional<Integer> savedId = taskService.save(task, user);
        if (savedId.isEmpty()) {
            model.addAttribute("hasAlert", true);
            model.addAttribute("task", task);
            model.addAttribute("priorities", priorityService.findAllByOrderByPosition());
            model.addAttribute("categories", categoryService.findAll());
            return "tasks/create";
        }
        return "redirect:/tasks/%s".formatted(savedId.get());
    }

    @PostMapping("/{id}/set-status")
    public String processSetStatus(@PathVariable int id, @RequestParam boolean done, Model model) {
        boolean hasChange = taskService.setStatusById(id, done);
        if (!hasChange) {
            model.addAttribute("hasAlert", true);
            return getById(id, model);
        }
        return "redirect:/tasks/%s".formatted(id);
    }

    @PostMapping("/{id}/delete")
    public String processDeleteById(@PathVariable int id, Model model) {
        boolean hasChange = taskService.deleteById(id);
        if (!hasChange) {
            model.addAttribute("hasAlert", true);
            return getById(id, model);
        }
        return "tasks/successDelete";
    }

    @GetMapping("/{id}/update")
    public String updateById(@PathVariable int id, Model model) {
        Optional<TaskUpdateDto> taskOptional = taskService.getTaskUpdateDtoById(id);
        if (taskOptional.isEmpty()) {
            model.addAttribute("message", "Задача не найдена.");
            return "error/404";
        }
        model.addAttribute("task", taskOptional.get());
        model.addAttribute("priorities", priorityService.findAllByOrderByPosition());
        model.addAttribute("categories", categoryService.findAll());
        return "tasks/update";
    }

    @PostMapping("/{id}/update")
    public String processUpdate(TaskUpdateDto task, @PathVariable int id, Model model) {
        boolean hasChange = taskService.update(task);
        if (!hasChange) {
            model.addAttribute("hasAlert", true);
            model.addAttribute("task", task);
            model.addAttribute("priorities", priorityService.findAllByOrderByPosition());
            model.addAttribute("categories", categoryService.findAll());
            return "tasks/update";
        }
        return "redirect:/tasks/%s".formatted(id);
    }
}
