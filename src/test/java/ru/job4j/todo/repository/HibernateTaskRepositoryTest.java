package ru.job4j.todo.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.todo.configuration.SessionFactoryConfiguration;
import ru.job4j.todo.model.Task;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.job4j.todo.util.TaskUtil.makeTask;

class HibernateTaskRepositoryTest {
    private static SessionFactory sessionFactory;
    private static HibernateTaskRepository taskRepository;

    @BeforeAll
    static void beforeAll() {
        sessionFactory = new SessionFactoryConfiguration().createSessionFactory();
        taskRepository = new HibernateTaskRepository(sessionFactory);
    }

    @AfterEach
    void tearDown() {
        taskRepository.findAllByOrderByCreatedDesc().stream().map(Task::getId).forEach(taskRepository::deleteById);
    }

    @AfterAll
    static void afterAll() {
        sessionFactory.close();
    }

    @Test
    void whenSaveTaskThenReturnTrueAndSetIdInTask() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        Task task = new Task();
        task.setTitle("title");
        task.setDescription("description");
        task.setCreated(now);

        boolean isSave = taskRepository.save(task);
        Task actualTask = taskRepository.findById(task.getId()).orElseThrow();

        assertThat(isSave).isTrue();
        assertThat(actualTask).usingRecursiveComparison().isEqualTo(task);
    }

    @Test
    void whenUpdateTaskCorrectThenReturnTrueAndTaskUpdated() {
        Task task = makeTask(7, false);
        task.setId(null);
        taskRepository.save(task);

        Task updateTask = new Task();
        updateTask.setId(task.getId());
        updateTask.setTitle("update task title");
        updateTask.setDescription("update task description");
        updateTask.setCreated(task.getCreated());
        boolean isUpdate = taskRepository.update(updateTask);
        Task actualTask = taskRepository.findById(updateTask.getId()).orElseThrow();

        assertThat(isUpdate).isTrue();
        Task expectedTask = new Task();
        expectedTask.setId(updateTask.getId());
        expectedTask.setTitle(updateTask.getTitle());
        expectedTask.setDescription(updateTask.getDescription());
        expectedTask.setDone(updateTask.isDone());
        expectedTask.setCreated(task.getCreated());
        assertThat(actualTask).usingRecursiveComparison().isEqualTo(expectedTask);
    }

    @Test
    void whenUpdateTaskNotCorrectThenReturnFalse() {
        Task task = makeTask(7, false);

        boolean isUpdate = taskRepository.update(task);
        Optional<Task> actualTask = taskRepository.findById(task.getId());

        assertThat(isUpdate).isFalse();
        assertThat(actualTask).isEmpty();
    }

    @Test
    void whenFindAllByOrderByCreatedDescThenGetTasksOrderedDesc() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        List<Task> tasks = List.of(
                makeTask(9, false),
                makeTask(8, false),
                makeTask(7, false)
        );
        tasks.forEach(task -> {
            task.setCreated(now.plusHours(task.getId()));
            task.setId(null);
        });
        tasks.forEach(taskRepository::save);

        List<Task> actualTasks = (List<Task>) taskRepository.findAllByOrderByCreatedDesc();

        assertThat(actualTasks).usingRecursiveComparison().isEqualTo(tasks);
    }

    @Test
    void whenFindAllByDoneTrueOrderByCreatedDescThenReturnTasksDoneAndCreatedDesc() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        List<Task> tasks = List.of(
                makeTask(9, true),
                makeTask(8, false),
                makeTask(7, true),
                makeTask(6, false),
                makeTask(5, true)
        );
        tasks.forEach(task -> {
            task.setCreated(now.plusHours(task.getId()));
            task.setId(null);
        });
        tasks.forEach(taskRepository::save);

        List<Task> actualTasks = (List<Task>) taskRepository.findAllByDoneOrderByCreatedDesc(true);
        List<Task> expectedTasks = tasks.stream().filter(task -> Objects.equals(task.isDone(), true)).toList();

        assertThat(actualTasks).usingRecursiveComparison().isEqualTo(expectedTasks);
    }

    @Test
    void whenFindAllByDoneFalseOrderByCreatedDescThenReturnTasksNotDoneAndCreatedDesc() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        List<Task> tasks = List.of(
                makeTask(9, true),
                makeTask(8, false),
                makeTask(7, true),
                makeTask(6, false),
                makeTask(5, true)
        );
        tasks.forEach(task -> {
            task.setCreated(now.plusHours(task.getId()));
            task.setId(null);
        });
        tasks.forEach(taskRepository::save);

        List<Task> actualTasks = (List<Task>) taskRepository.findAllByDoneOrderByCreatedDesc(false);
        List<Task> expectedTasks = tasks.stream().filter(task -> Objects.equals(task.isDone(), false)).toList();

        assertThat(actualTasks).usingRecursiveComparison().isEqualTo(expectedTasks);
    }

    @Test
    void whenSetStatusByIdCorrectThenReturnTrueAndChangeStatus() {
        Task task = makeTask(7, false);
        task.setId(null);
        taskRepository.save(task);

        boolean hasChange = taskRepository.setStatusById(task.getId(), true);
        Task actualTask = taskRepository.findById(task.getId()).orElseThrow();

        assertThat(hasChange).isTrue();
        assertThat(actualTask.isDone()).isTrue();
    }

    @Test
    void whenSetStatusByIdNotCorrectThenReturnFalse() {
        boolean hasChange = taskRepository.setStatusById(7, true);

        assertThat(hasChange).isFalse();
    }

    @Test
    void whenDeleteByIdCorrectThenReturnTrue() {
        Task task = makeTask(7, false);
        task.setId(null);
        taskRepository.save(task);

        boolean isDelete = taskRepository.deleteById(task.getId());
        Optional<Task> actualTask = taskRepository.findById(task.getId());

        assertThat(isDelete).isTrue();
        assertThat(actualTask).isEmpty();
    }

    @Test
    void whenDeleteByIdNotCorrectThenReturnFalse() {
        boolean isDelete = taskRepository.deleteById(7);

        assertThat(isDelete).isFalse();
    }
}