package ru.job4j.todo.controller;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.todo.dto.TaskCreateDto;
import ru.job4j.todo.dto.TaskUpdateDto;
import ru.job4j.todo.model.Category;
import ru.job4j.todo.model.Priority;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.CategoryService;
import ru.job4j.todo.service.PriorityService;
import ru.job4j.todo.service.TaskWithTimezoneService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.job4j.todo.util.TaskUtil.makeTask;

class TaskControllerTest {
    private TaskController taskController;
    private TaskWithTimezoneService taskService;
    private PriorityService priorityService;
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        taskService = mock(TaskWithTimezoneService.class);
        priorityService = mock(PriorityService.class);
        categoryService = mock(CategoryService.class);
        taskController = new TaskController(taskService, priorityService, categoryService);
    }

    @Test
    void whenGetAllPageThenReturnViewWithTasks() {
        Collection<Task> tasks = List.of(
                makeTask(1, true),
                makeTask(2, true),
                makeTask(3, true)
        );
        when(taskService.findAllByOrderByCreatedDesc(any())).thenReturn(tasks);
        ConcurrentModel model = new ConcurrentModel();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new User());

        String view = taskController.getAll(model, session);
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
        when(taskService.findAllByDoneOrderByCreatedDesc(anyBoolean(), any())).thenReturn(tasks);
        ConcurrentModel model = new ConcurrentModel();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new User());

        String view = taskController.getAllNew(model, session);
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
        when(taskService.findAllByDoneOrderByCreatedDesc(anyBoolean(), any())).thenReturn(tasks);
        ConcurrentModel model = new ConcurrentModel();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new User());

        String view = taskController.getAllDone(model, session);
        Object actualTasks = model.getAttribute("tasks");

        assertThat(view).isEqualTo("tasks/list");
        assertThat(actualTasks).isEqualTo(tasks);
    }

    @Test
    void whenGetByIdPageThenReturnViewWithTask() {
        Task task = makeTask(1, false);
        when(taskService.findById(anyInt(), any())).thenReturn(Optional.of(task));
        ConcurrentModel model = new ConcurrentModel();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new User());

        String view = taskController.getById(1, model, session);
        Task actualTask = (Task) model.getAttribute("task");

        assertThat(view).isEqualTo("tasks/one");
        assertThat(actualTask).isEqualTo(task);
    }

    @Test
    void whenGetByWrongIdThenReturnView404WithMessage() {
        when(taskService.findById(anyInt(), any())).thenReturn(Optional.empty());
        ConcurrentModel model = new ConcurrentModel();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new User());

        String view = taskController.getById(1, model, session);
        String message = (String) model.getAttribute("message");

        assertThat(view).isEqualTo("error/404");
        assertThat(message).isEqualTo("Задача не найдена.");
    }

    @Test
    void whenGetCreatePageThenReturnViewWithEmptyTaskCreateDto() {
        ConcurrentModel model = new ConcurrentModel();
        Mockito.when(taskService.getEmptyTaskCreateDto())
                .thenReturn(new TaskCreateDto(null, null, null, null));

        String view = taskController.create(model);
        TaskCreateDto actualTask = (TaskCreateDto) model.getAttribute("task");

        assertThat(view).isEqualTo("tasks/create");
        assertThat(actualTask).isEqualTo(new TaskCreateDto(
                null,
                null,
                null,
                null
        ));
    }

    @Test
    void whenProcessCreatingTaskCorrectThenReturnRedirectTaskPage() {
        TaskCreateDto taskCreateDto = new TaskCreateDto("Title", Set.of(1, 2), 1, "Description");
        User user = new User();
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("user")).thenReturn(user);
        when(taskService.save(taskCreateDto, user)).thenReturn(Optional.of(7));

        String view = taskController.processCreate(taskCreateDto, new ConcurrentModel(), session);

        assertThat(view).isEqualTo("redirect:/tasks/7");
    }

    @Test
    @SuppressWarnings(value = "unchecked")
    void whenProcessCreatingTaskWrongThenReturnCreatePageWIthTaskAndAlert() {
        HttpSession session = mock(HttpSession.class);
        TaskCreateDto taskCreateDto = new TaskCreateDto("Title", Set.of(1, 2), 1, "Description");
        User user = new User();
        when(session.getAttribute("user")).thenReturn(user);
        when(taskService.save(taskCreateDto, user)).thenReturn(Optional.empty());
        Set<Priority> priorities = Set.of(new Priority());
        when(priorityService.findAllByOrderByPosition()).thenReturn(priorities);
        Set<Category> categories = Set.of(new Category());
        when(categoryService.findAll()).thenReturn(categories);

        ConcurrentModel model = new ConcurrentModel();
        String view = taskController.processCreate(taskCreateDto, model, new MockHttpSession());
        Boolean hasAlert = (Boolean) model.getAttribute("hasAlert");
        TaskCreateDto actualTask = (TaskCreateDto) model.getAttribute("task");
        Set<Priority> actualPriorities = (Set<Priority>) model.getAttribute("priorities");
        Set<Category> actualCategories = (Set<Category>) model.getAttribute("categories");

        assertThat(view).isEqualTo("tasks/create");
        assertThat(hasAlert).isTrue();
        assertThat(actualTask).usingRecursiveComparison().isEqualTo(taskCreateDto);
        assertThat(actualPriorities).isEqualTo(priorities);
        assertThat(actualCategories).isEqualTo(categories);
    }

    @Test
    void whenProcessSetStatusInTaskCorrectThenReturnRedirectTaskPage() {
        Task task = makeTask(7, false);
        when(taskService.setStatusById(task.getId(), true)).thenReturn(true);
        ConcurrentModel model = new ConcurrentModel();
        String view = taskController.processSetStatus(task.getId(), true, model, new MockHttpSession());

        assertThat(view).isEqualTo("redirect:/tasks/7");
    }

    @Test
    void whenProcessSetStatusInTaskWrongThenReturnPageTaskWithAlert() {
        Task task = makeTask(7, false);
        when(taskService.setStatusById(task.getId(), true)).thenReturn(false);
        when(taskService.findById(anyInt(), any())).thenReturn(Optional.of(task));
        ConcurrentModel model = new ConcurrentModel();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new User());

        String view = taskController.processSetStatus(task.getId(), true, model, session);
        Boolean hasAlert = (Boolean) model.getAttribute("hasAlert");

        assertThat(view).isEqualTo("tasks/one");
        assertThat(hasAlert).isTrue();
    }

    @Test
    void whenProcessDeleteTaskThenReturnPageSuccessDelete() {
        when(taskService.deleteById(anyInt())).thenReturn(true);

        ConcurrentModel model = new ConcurrentModel();
        String view = taskController.processDeleteById(1, model, new MockHttpSession());

        assertThat(view).isEqualTo("tasks/successDelete");
    }

    @Test
    void whenProcessDeleteTaskWrongThenReturnPageTaskWithAlert() {
        when(taskService.deleteById(anyInt())).thenReturn(false);
        when(taskService.findById(anyInt(), any())).thenReturn(Optional.of(new Task()));
        ConcurrentModel model = new ConcurrentModel();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new User());

        String view = taskController.processDeleteById(1, model, session);
        Boolean hasAlert = (Boolean) model.getAttribute("hasAlert");

        assertThat(view).isEqualTo("tasks/one");
        assertThat(hasAlert).isTrue();
    }

    @Test
    @SuppressWarnings("unchecked")
    void whenGetPageUpdateByIdThenGetPageWithTask() {
        TaskUpdateDto taskUpdateDto = new TaskUpdateDto(
                1, "Title", "description",
                Set.of(1), 1, false, LocalDateTime.now()
        );
        Set<Priority> priorities = Set.of(new Priority());
        Set<Category> categories = Set.of(new Category());
        when(taskService.getTaskUpdateDtoById(anyInt(), any())).thenReturn(Optional.of(taskUpdateDto));
        when(priorityService.findAllByOrderByPosition()).thenReturn(priorities);
        when(categoryService.findAll()).thenReturn(categories);
        ConcurrentModel model = new ConcurrentModel();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new User());

        String view = taskController.updateById(taskUpdateDto.id(), model, session);
        TaskUpdateDto actualTask = (TaskUpdateDto) model.getAttribute("task");
        Set<Priority> actualPriorities = (Set<Priority>) model.getAttribute("priorities");
        Set<Category> actualCategories = (Set<Category>) model.getAttribute("categories");

        assertThat(view).isEqualTo("tasks/update");
        assertThat(actualTask).usingRecursiveComparison().isEqualTo(taskUpdateDto);
        assertThat(actualPriorities).isEqualTo(priorities);
        assertThat(actualCategories).isEqualTo(categories);
    }

    @Test
    void whenGetPageUpdateByIdNoCorrectIdThenGetPage404WithMessage() {
        when(taskService.findById(anyInt(), any())).thenReturn(Optional.empty());
        ConcurrentModel model = new ConcurrentModel();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new User());

        String view = taskController.updateById(1, model, session);
        String message = (String) model.getAttribute("message");

        assertThat(view).isEqualTo("error/404");
        assertThat(message).isEqualTo("Задача не найдена.");
    }

    @Test
    void whenProcessUpdateCorrectThenRedirectPageTask() {
        TaskUpdateDto taskUpdateDto = new TaskUpdateDto(
                1, "Title", "description",
                Set.of(1), 1, false, LocalDateTime.now()
        );
        when(taskService.update(taskUpdateDto)).thenReturn(true);

        ConcurrentModel model = new ConcurrentModel();
        String view = taskController.processUpdate(taskUpdateDto, 7, model);

        assertThat(view).isEqualTo("redirect:/tasks/7");
    }

    @Test
    @SuppressWarnings("unchecked")
    void whenProcessUpdateNotCorrectThenGetPageUpdateWithAlertAndTask() {
        TaskUpdateDto taskUpdateDto = new TaskUpdateDto(
                1, "Title", "description",
                Set.of(1), 1, false, LocalDateTime.now()
        );
        Set<Priority> priorities = Set.of(new Priority());
        Set<Category> categories = Set.of(new Category());
        when(taskService.update(taskUpdateDto)).thenReturn(false);
        when(priorityService.findAllByOrderByPosition()).thenReturn(priorities);
        when(categoryService.findAll()).thenReturn(categories);

        ConcurrentModel model = new ConcurrentModel();
        String view = taskController.processUpdate(taskUpdateDto, 7, model);
        Boolean hasAlert = (Boolean) model.getAttribute("hasAlert");
        TaskUpdateDto actualTask = (TaskUpdateDto) model.getAttribute("task");
        Set<Priority> actualPriorities = (Set<Priority>) model.getAttribute("priorities");
        Set<Category> actualCategories = (Set<Category>) model.getAttribute("categories");

        assertThat(view).isEqualTo("tasks/update");
        assertThat(hasAlert).isTrue();
        assertThat(actualTask).usingRecursiveComparison().isEqualTo(taskUpdateDto);
        assertThat(actualPriorities).isEqualTo(priorities);
        assertThat(actualCategories).isEqualTo(categories);
    }
}