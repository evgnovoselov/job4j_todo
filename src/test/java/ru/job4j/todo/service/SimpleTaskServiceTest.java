package ru.job4j.todo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.job4j.todo.dto.TaskCreateDto;
import ru.job4j.todo.dto.TaskUpdateDto;
import ru.job4j.todo.mapper.TaskMapper;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;
import ru.job4j.todo.repository.CategoryRepository;
import ru.job4j.todo.repository.PriorityRepository;
import ru.job4j.todo.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static ru.job4j.todo.util.TaskUtil.makeTask;

class SimpleTaskServiceTest {
    private SimpleTaskService taskService;
    private TaskRepository taskRepository;
    private TaskMapper taskMapper;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        CategoryRepository categoryRepository = mock(CategoryRepository.class);
        PriorityRepository priorityRepository = mock(PriorityRepository.class);
        taskMapper = mock(TaskMapper.class);
        taskService = new SimpleTaskService(
                taskRepository,
                categoryRepository,
                priorityRepository,
                taskMapper
        );
    }

    @Test
    void whenGetEmptyTaskCreateDtoThenReturn() {
        TaskCreateDto taskCreateDto = new TaskCreateDto(null, null, null, null);
        when(taskMapper.toTaskCreateDto(any())).thenReturn(taskCreateDto);

        TaskCreateDto emptyTaskCreateDto = taskService.getEmptyTaskCreateDto();

        assertThat(emptyTaskCreateDto).isEqualTo(taskCreateDto);
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
        when(taskRepository.findAllByDoneOrderByCreatedDesc(true)).thenReturn(tasks);

        Collection<Task> actualTasks = taskService.findAllByDoneOrderByCreatedDesc(true);

        assertThat(actualTasks).usingRecursiveComparison().isEqualTo(tasks);
    }

    @Test
    void whenFindAllByDoneFalseOrderByCreatedDescThenReturnCollectionTask() {
        List<Task> tasks = List.of(
                makeTask(9, false),
                makeTask(8, false),
                makeTask(7, false)
        );
        when(taskRepository.findAllByDoneOrderByCreatedDesc(false)).thenReturn(tasks);

        Collection<Task> actualTasks = taskService.findAllByDoneOrderByCreatedDesc(false);

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
    void whenSaveTaskWithTitleThenSaveTaskWithSetCreatedAndDoneAndReturnSavedId() {
        TaskCreateDto taskCreateDto = new TaskCreateDto("Title", Set.of(1, 2), 1, "Description");
        User user = makeUser();
        Task task = new Task();
        task.setTitle(taskCreateDto.title());
        when(taskMapper.toTask(any(TaskCreateDto.class), any(), any())).thenReturn(task);
        when(taskRepository.save(any())).thenReturn(Optional.of(1));

        Optional<Integer> savedId = taskService.save(taskCreateDto, user);

        assertThat(savedId).isPresent();
        assertThat(savedId.get()).isEqualTo(1);
    }

    private static User makeUser() {
        User user = new User();
        user.setId(1);
        user.setLogin("login");
        user.setName("name");
        user.setPassword("password");
        return user;
    }

    @Test
    void whenSaveTaskWithoutTitleThenSaveTaskWithSetCreatedAndDoneAndReturnSavedId() {
        TaskCreateDto taskCreateDto = new TaskCreateDto("Title", Set.of(1, 2), 1, "Description");
        User user = makeUser();
        Task task = new Task();
        task.setDescription("Description");
        when(taskMapper.toTask(any(TaskCreateDto.class), any(), any())).thenReturn(task);
        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        when(taskRepository.save(taskArgumentCaptor.capture())).thenReturn(Optional.of(1));

        Optional<Integer> savedId = taskService.save(taskCreateDto, user);
        Task actualTask = taskArgumentCaptor.getValue();

        assertThat(savedId).isPresent();
        assertThat(savedId.get()).isEqualTo(1);
        Task expectedTask = new Task();
        expectedTask.setTitle("Задача без названия");
        expectedTask.setDescription("Description");
        expectedTask.setDone(false);
        expectedTask.setUser(user);
        expectedTask.setCreated(actualTask.getCreated());
        assertThat(actualTask).usingRecursiveComparison().isEqualTo(expectedTask);
    }

    @Test
    void whenSaveTaskWithTitleBlankThenSaveTaskWithSetCreatedAndDoneAndReturnSavedId() {
        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        when(taskRepository.save(taskArgumentCaptor.capture())).thenReturn(Optional.of(1));
        Task task = new Task();
        task.setTitle("    ");
        task.setDescription("Description");
        User user = makeUser();
        TaskCreateDto taskCreateDto = new TaskCreateDto("Title", Set.of(1, 2), 1, "Description");
        when(taskMapper.toTask(any(TaskCreateDto.class), any(), any())).thenReturn(task);

        Optional<Integer> savedId = taskService.save(taskCreateDto, user);
        Task actualTask = taskArgumentCaptor.getValue();

        assertThat(savedId).isPresent();
        assertThat(savedId.get()).isEqualTo(1);
        Task expectedTask = new Task();
        expectedTask.setTitle("Задача без названия");
        expectedTask.setDescription("Description");
        expectedTask.setCreated(task.getCreated());
        expectedTask.setDone(false);
        expectedTask.setUser(user);
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
        TaskUpdateDto taskUpdateDto = new TaskUpdateDto(1, "Title", "Description", Set.of(1, 2), 1, false, LocalDateTime.now());
        Task oldTask = makeTask(7, false);
        Task task = makeTask(7, false);
        task.setTitle("Updated task");
        task.setCreated(oldTask.getCreated().plusHours(3));
        when(taskMapper.toTask(any(TaskUpdateDto.class), any(), any())).thenReturn(task);
        when(taskRepository.findById(anyInt())).thenReturn(Optional.of(oldTask));
        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        when(taskRepository.update(taskArgumentCaptor.capture())).thenReturn(true);

        boolean hasChange = taskService.update(taskUpdateDto);
        Task actualTask = taskArgumentCaptor.getValue();

        assertThat(hasChange).isTrue();
        Task expectedTask = new Task();
        expectedTask.setId(task.getId());
        expectedTask.setTitle(task.getTitle());
        expectedTask.setDescription(task.getDescription());
        expectedTask.setDone(task.isDone());
        expectedTask.setCreated(oldTask.getCreated());
        expectedTask.setCategories(task.getCategories());
        expectedTask.setPriority(task.getPriority());
        assertThat(actualTask).usingRecursiveComparison().isEqualTo(expectedTask);
    }

    @Test
    void whenUpdateTaskNotHaveOldThenReturnFalse() {
        TaskUpdateDto taskUpdateDto = new TaskUpdateDto(1, "Title", "Description", Set.of(1, 2), 1, false, LocalDateTime.now());
        Task task = new Task();
        task.setId(1);
        when(taskMapper.toTask(any(TaskUpdateDto.class), any(), any())).thenReturn(task);
        when(taskRepository.findById(anyInt())).thenReturn(Optional.empty());

        boolean hasChange = taskService.update(taskUpdateDto);

        assertThat(hasChange).isFalse();
    }

    @Test
    void whenGetTaskUpdateDtoByIdThenReturnTaskUpdateDto() {
        when(taskRepository.findById(anyInt())).thenReturn(Optional.of(new Task()));
        TaskUpdateDto taskUpdateDto = new TaskUpdateDto(
                1,
                "Title",
                "Description",
                Set.of(1, 2),
                1,
                false,
                LocalDateTime.now()
        );
        when(taskMapper.toTaskUpdateDto(new Task())).thenReturn(taskUpdateDto);

        Optional<TaskUpdateDto> actualTaskUpdateDto = taskService.getTaskUpdateDtoById(1);

        assertThat(actualTaskUpdateDto).isPresent();
        TaskUpdateDto expectedTaskUpdateDto = new TaskUpdateDto(
                1,
                "Title",
                "Description",
                Set.of(1, 2),
                1,
                false,
                taskUpdateDto.created()
        );
        assertThat(actualTaskUpdateDto.get()).usingRecursiveComparison().isEqualTo(expectedTaskUpdateDto);
    }

    @Test
    void whenIdNotHaveInGetTaskUpdateDtoByIdThenReturnEmpty() {
        when(taskRepository.findById(anyInt())).thenReturn(Optional.empty());

        Optional<TaskUpdateDto> taskUpdateDtoById = taskService.getTaskUpdateDtoById(1);

        assertThat(taskUpdateDtoById).isEmpty();
    }
}