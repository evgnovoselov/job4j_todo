package ru.job4j.todo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.service.TaskService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TaskControllerTest {
    private TaskController taskController;
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = mock(TaskService.class);
        taskController = new TaskController(taskService);
    }

    private Task makeTask(int seed, boolean done) {
        LocalDateTime dateTime = LocalDateTime.of(2023, 8, 1, 8, 10);
        Task task = new Task();
        task.setId(seed);
        task.setTitle("Task title " + seed);
        task.setDescription("Task description " + seed);
        task.setCreated(dateTime);
        task.setDone(done);
        return task;
    }

    @Test
    void whenGetAllPageThenReturnViewWithTasks() {
        Collection<Task> tasks = List.of(
                makeTask(1, true),
                makeTask(2, true),
                makeTask(3, true)
        );
        when(taskService.findAllByOrderByCreatedDesc()).thenReturn(tasks);

        ConcurrentModel model = new ConcurrentModel();
        String view = taskController.getAll(model);
        Object actualTasks = model.getAttribute("tasks");

        assertThat(view).isEqualTo("tasks/list");
        assertThat(actualTasks).isEqualTo(tasks);
    }

    @Test
    void whenGetAllNewPageThenReturnViewWithTasks() {
        Collection<Task> tasks = List.of(
                makeTask(1, true),
                makeTask(2, true),
                makeTask(3, true)
        );
        when(taskService.findAllByDoneFalseOrderByCreatedDesc()).thenReturn(tasks);

        ConcurrentModel model = new ConcurrentModel();
        String view = taskController.getAllNew(model);
        Object actualTasks = model.getAttribute("tasks");

        assertThat(view).isEqualTo("tasks/list");
        assertThat(actualTasks).isEqualTo(tasks);
    }

    @Test
    void whenGetAllDonePageThenReturnViewWithTasks() {
        Collection<Task> tasks = List.of(
                makeTask(1, true),
                makeTask(2, true),
                makeTask(3, true)
        );
        when(taskService.findAllByDoneTrueOrderByCreatedDesc()).thenReturn(tasks);

        ConcurrentModel model = new ConcurrentModel();
        String view = taskController.getAllDone(model);
        Object actualTasks = model.getAttribute("tasks");

        assertThat(view).isEqualTo("tasks/list");
        assertThat(actualTasks).isEqualTo(tasks);
    }

    @Test
    void whenGetByIdPageThenReturnViewWithTask() {
        Task task = makeTask(1, false);
        when(taskService.findById(anyInt())).thenReturn(Optional.of(task));

        ConcurrentModel model = new ConcurrentModel();
        String view = taskController.getById(1, model);
        Task actualTask = (Task) model.getAttribute("task");

        assertThat(view).isEqualTo("tasks/one");
        assertThat(actualTask).isEqualTo(task);
    }

    @Test
    void whenGetByWrongIdThenReturnView404WithMessage() {
        when(taskService.findById(anyInt())).thenReturn(Optional.empty());

        ConcurrentModel model = new ConcurrentModel();
        String view = taskController.getById(1, model);
        String message = (String) model.getAttribute("message");

        assertThat(view).isEqualTo("error/404");
        assertThat(message).isEqualTo("Задача не найдена.");
    }

    @Test
    void whenGetCreatePageThenReturnViewWithEmptyTask() {
        Task task = new Task();

        ConcurrentModel model = new ConcurrentModel();
        String view = taskController.create(model);
        Task actualTask = (Task) model.getAttribute("task");

        assertThat(view).isEqualTo("tasks/create");
        assertThat(actualTask).isEqualTo(task);
    }

    @Test
    void whenProcessCreatingTaskCorrectThenReturnRedirectTaskPage() {
        Task task = new Task();
        task.setId(7);
        when(taskService.save(task)).thenReturn(true);

        String view = taskController.processCreate(task, new ConcurrentModel());

        assertThat(view).isEqualTo("redirect:/tasks/7");
    }

    @Test
    void whenProcessCreatingTaskWrongThenReturnCreatePageWIthTaskAndAlert() {
        Task task = makeTask(1, true);
        when(taskService.save(task)).thenReturn(false);

        ConcurrentModel model = new ConcurrentModel();
        String view = taskController.processCreate(task, model);
        Boolean hasAlert = (Boolean) model.getAttribute("hasAlert");
        Task actualTask = (Task) model.getAttribute("task");

        assertThat(view).isEqualTo("tasks/create");
        assertThat(hasAlert).isTrue();
        assertThat(actualTask).usingRecursiveComparison().isEqualTo(task);
    }

    @Test
    void whenProcessSetStatusInTaskCorrectThenReturnRedirectTaskPage() {
        Task task = makeTask(7, false);
        when(taskService.setStatusById(task.getId(), true)).thenReturn(true);

        ConcurrentModel model = new ConcurrentModel();
        String view = taskController.processSetStatus(task.getId(), true, model);

        assertThat(view).isEqualTo("redirect:/tasks/7");
    }

    @Test
    void whenProcessSetStatusInTaskWrongThenReturnPageTaskWithAlert() {
        Task task = makeTask(7, false);
        when(taskService.setStatusById(task.getId(), true)).thenReturn(false);
        when(taskService.findById(task.getId())).thenReturn(Optional.of(task));

        ConcurrentModel model = new ConcurrentModel();
        String view = taskController.processSetStatus(task.getId(), true, model);
        Boolean hasAlert = (Boolean) model.getAttribute("hasAlert");

        assertThat(view).isEqualTo("tasks/one");
        assertThat(hasAlert).isTrue();
    }

    @Test
    void whenProcessDeleteTaskThenReturnPageSuccessDelete() {
        when(taskService.deleteById(anyInt())).thenReturn(true);

        ConcurrentModel model = new ConcurrentModel();
        String view = taskController.processDeleteById(1, model);

        assertThat(view).isEqualTo("tasks/successDelete");
    }

    @Test
    void whenProcessDeleteTaskWrongThenReturnPageTaskWithAlert() {
        when(taskService.deleteById(anyInt())).thenReturn(false);
        when(taskService.findById(anyInt())).thenReturn(Optional.of(new Task()));

        ConcurrentModel model = new ConcurrentModel();
        String view = taskController.processDeleteById(1, model);
        Boolean hasAlert = (Boolean) model.getAttribute("hasAlert");

        assertThat(view).isEqualTo("tasks/one");
        assertThat(hasAlert).isTrue();
    }

    @Test
    void whenGetPageUpdateByIdThenGetPageWithTask() {
        Task task = makeTask(7, false);
        when(taskService.findById(task.getId())).thenReturn(Optional.of(task));

        ConcurrentModel model = new ConcurrentModel();
        String view = taskController.updateById(task.getId(), model);
        Task actualTask = (Task) model.getAttribute("task");

        assertThat(view).isEqualTo("tasks/update");
        assertThat(actualTask).usingRecursiveComparison().isEqualTo(task);
    }

    @Test
    void whenGetPageUpdateByIdNoCorrectIdThenGetPage404WithMessage() {
        when(taskService.findById(anyInt())).thenReturn(Optional.empty());

        ConcurrentModel model = new ConcurrentModel();
        String view = taskController.updateById(1, model);
        String message = (String) model.getAttribute("message");

        assertThat(view).isEqualTo("error/404");
        assertThat(message).isEqualTo("Задача не найдена.");
    }

    @Test
    void whenProcessUpdateCorrectThenRedirectPageTask() {
        Task task = makeTask(7, false);
        when(taskService.update(task)).thenReturn(true);

        ConcurrentModel model = new ConcurrentModel();
        String view = taskController.processUpdate(task, 7, model);

        assertThat(view).isEqualTo("redirect:/tasks/7");
    }

    @Test
    void whenProcessUpdateNotCorrectThenGetPageUpdateWithAlertAndTask() {
        Task task = makeTask(7, false);
        when(taskService.update(task)).thenReturn(false);

        ConcurrentModel model = new ConcurrentModel();
        String view = taskController.processUpdate(task, 7, model);
        Boolean hasAlert = (Boolean) model.getAttribute("hasAlert");
        Task actualTask = (Task) model.getAttribute("task");

        assertThat(view).isEqualTo("tasks/update");
        assertThat(hasAlert).isTrue();
        assertThat(actualTask).usingRecursiveComparison().isEqualTo(task);
    }
}