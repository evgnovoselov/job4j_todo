package ru.job4j.todo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.repository.TaskRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static ru.job4j.todo.util.TaskUtil.makeTask;

class SimpleTaskServiceTest {
    private SimpleTaskService taskService;
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        taskService = new SimpleTaskService(taskRepository);
    }

    @Test
    void whenFindAllByOrderCreatedDescThenReturnCollectionTask() {
        List<Task> tasks = List.of(
                makeTask(9, false),
                makeTask(8, false),
                makeTask(7, false)
        );
        when(taskRepository.findAllByOrderByCreatedDesc()).thenReturn(tasks);

        Collection<Task> actualTasks = taskService.findAllByOrderByCreatedDesc();

        assertThat(actualTasks).usingRecursiveComparison().isEqualTo(tasks);
    }

    @Test
    void whenFindAllByDoneTrueOrderByCreatedDescThenReturnCollectionTask() {
        List<Task> tasks = List.of(
                makeTask(9, true),
                makeTask(8, true),
                makeTask(7, true)
        );
        when(taskRepository.findAllByDoneTrueOrderByCreatedDesc()).thenReturn(tasks);

        Collection<Task> actualTasks = taskService.findAllByDoneTrueOrderByCreatedDesc();

        assertThat(actualTasks).usingRecursiveComparison().isEqualTo(tasks);
    }

    @Test
    void whenFindAllByDoneFalseOrderByCreatedDescThenReturnCollectionTask() {
        List<Task> tasks = List.of(
                makeTask(9, false),
                makeTask(8, false),
                makeTask(7, false)
        );
        when(taskRepository.findAllByDoneFalseOrderByCreatedDesc()).thenReturn(tasks);

        Collection<Task> actualTasks = taskService.findAllByDoneFalseOrderByCreatedDesc();

        assertThat(actualTasks).usingRecursiveComparison().isEqualTo(tasks);
    }

    @Test
    void whenFindByIdThenReturnOptionalTask() {
        Task task = makeTask(7, false);
        when(taskRepository.findById(7)).thenReturn(Optional.of(task));

        Task actualTask = taskService.findById(7).orElseThrow();

        assertThat(actualTask).usingRecursiveComparison().isEqualTo(task);
    }

    @Test
    void whenSaveTaskWithTitleThenSaveTaskWithSetCreatedAndDoneAndReturnTrue() {
        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        when(taskRepository.save(taskArgumentCaptor.capture())).thenReturn(true);
        Task task = new Task();
        task.setTitle("title");
        task.setDescription("description");

        boolean hasSave = taskService.save(task);
        Task actualTask = taskArgumentCaptor.getValue();

        assertThat(hasSave).isTrue();
        Task expectedTask = new Task();
        expectedTask.setTitle("title");
        expectedTask.setDescription("description");
        expectedTask.setCreated(task.getCreated());
        expectedTask.setDone(false);
        assertThat(actualTask).usingRecursiveComparison().isEqualTo(expectedTask);
    }

    @Test
    void whenSaveTaskWithoutTitleThenSaveTaskWithSetCreatedAndDoneAndReturnTrue() {
        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        when(taskRepository.save(taskArgumentCaptor.capture())).thenReturn(true);
        Task task = new Task();
        task.setDescription("description");

        boolean hasSave = taskService.save(task);
        Task actualTask = taskArgumentCaptor.getValue();

        assertThat(hasSave).isTrue();
        Task expectedTask = new Task();
        expectedTask.setTitle("Задача без названия");
        expectedTask.setDescription("description");
        expectedTask.setCreated(task.getCreated());
        expectedTask.setDone(false);
        assertThat(actualTask).usingRecursiveComparison().isEqualTo(expectedTask);
    }

    @Test
    void whenSaveTaskWithTitleBlankThenSaveTaskWithSetCreatedAndDoneAndReturnTrue() {
        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        when(taskRepository.save(taskArgumentCaptor.capture())).thenReturn(true);
        Task task = new Task();
        task.setTitle("    ");
        task.setDescription("description");

        boolean hasSave = taskService.save(task);
        Task actualTask = taskArgumentCaptor.getValue();

        assertThat(hasSave).isTrue();
        Task expectedTask = new Task();
        expectedTask.setTitle("Задача без названия");
        expectedTask.setDescription("description");
        expectedTask.setCreated(task.getCreated());
        expectedTask.setDone(false);
        assertThat(actualTask).usingRecursiveComparison().isEqualTo(expectedTask);
    }

    @Test
    void whenSetStatusByIdThenReturnTrue() {
        when(taskRepository.setStatusById(anyInt(), anyBoolean())).thenReturn(true);

        boolean hasChange = taskService.setStatusById(7, true);

        assertThat(hasChange).isTrue();
    }

    @Test
    void whenDeleteByIdThenReturnTrue() {
        when(taskRepository.deleteById(anyInt())).thenReturn(true);

        boolean hasChange = taskService.deleteById(7);

        assertThat(hasChange).isTrue();
    }

    @Test
    void whenUpdateTaskThenReturnTrue() {
        when(taskRepository.update(any(Task.class))).thenReturn(true);

        boolean hasChange = taskService.update(new Task());

        assertThat(hasChange).isTrue();
    }
}