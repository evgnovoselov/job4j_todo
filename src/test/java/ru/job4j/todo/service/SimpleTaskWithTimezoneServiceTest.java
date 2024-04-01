package ru.job4j.todo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.job4j.todo.dto.TaskUpdateDto;
import ru.job4j.todo.mapper.TaskMapper;
import ru.job4j.todo.model.Priority;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.repository.CategoryRepository;
import ru.job4j.todo.repository.PriorityRepository;
import ru.job4j.todo.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.job4j.todo.util.TaskUtil.makeTask;

class SimpleTaskWithTimezoneServiceTest {
    private SimpleTaskWithTimezoneService taskWithTimezoneService;
    private TaskRepository taskRepository;
    private TaskMapper taskMapper;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        taskMapper = mock(TaskMapper.class);
        taskWithTimezoneService = new SimpleTaskWithTimezoneService(
                taskRepository,
                mock(CategoryRepository.class),
                mock(PriorityRepository.class),
                taskMapper
        );
    }

    @Test
    void whenFindAllByOrderCreatedDescWithTimezoneThenReturnCollectionTaskWithEditedCreatedToTimezone() {
        when(taskRepository.findAllByOrderByCreatedDesc()).thenReturn(List.of(
                makeTask(1, false),
                makeTask(2, false)
        ));

        Collection<Task> actualTasks = taskWithTimezoneService
                .findAllByOrderByCreatedDesc("Europe/Moscow");

        assertThat(actualTasks).usingRecursiveComparison().isEqualTo(Stream.of(
                makeTask(1, false),
                makeTask(2, false)
        ).peek(task -> task.setCreated(task.getCreated().plusHours(3))).toList());
    }

    @Test
    void whenFindAllByOrderCreatedDescWithInvalidTimezoneThenReturnCollectionTaskWithNotEditedCreated() {
        when(taskRepository.findAllByOrderByCreatedDesc()).thenReturn(List.of(
                makeTask(1, false),
                makeTask(2, false)
        ));

        Collection<Task> actualTasks = taskWithTimezoneService
                .findAllByOrderByCreatedDesc("invalidTimezone");

        assertThat(actualTasks).usingRecursiveComparison().isEqualTo(List.of(
                makeTask(1, false),
                makeTask(2, false)
        ));
    }

    @Test
    void whenFindAllByDoneOrderByCreatedDescWithTimezoneThenReturnCollectionTaskWithEditedCreatedToTimezone() {
        when(taskRepository.findAllByDoneOrderByCreatedDesc(anyBoolean())).thenReturn(List.of(
                makeTask(1, true),
                makeTask(2, true)
        ));

        Collection<Task> actualTasks = taskWithTimezoneService
                .findAllByDoneOrderByCreatedDesc(true, "Europe/Moscow");

        assertThat(actualTasks).usingRecursiveComparison().isEqualTo(Stream.of(
                makeTask(1, true),
                makeTask(2, true)
        ).peek(task -> task.setCreated(task.getCreated().plusHours(3))).toList());
    }

    @Test
    void whenFindAllByDoneOrderByCreatedDescWithInvalidTimezoneThenReturnCollectionTaskWithNotEditedCreated() {
        when(taskRepository.findAllByDoneOrderByCreatedDesc(anyBoolean())).thenReturn(List.of(
                makeTask(1, true),
                makeTask(2, true)
        ));

        Collection<Task> actualTasks = taskWithTimezoneService
                .findAllByDoneOrderByCreatedDesc(true, "invalidTimezone");

        assertThat(actualTasks).usingRecursiveComparison().isEqualTo(List.of(
                makeTask(1, true),
                makeTask(2, true)
        ));
    }

    @Test
    void whenFindByIdWithTimezoneThenReturnOptionalTaskWithEditedCreatedToTimezone() {
        Task task = makeTask(1, false);
        when(taskRepository.findById(anyInt())).thenReturn(Optional.of(task));

        Task actualTask = taskWithTimezoneService.findById(1, "Europe/Moscow").orElseThrow();

        assertThat(actualTask).usingRecursiveComparison().isEqualTo(Stream.of(
                makeTask(1, false)
        ).peek(tsk -> tsk.setCreated(tsk.getCreated().plusHours(3))).findFirst().get());
    }

    @Test
    void whenFindByIdWithInvalidTimezoneThenReturnOptionalTaskWithNotEditedCreated() {
        Task task = makeTask(1, false);
        when(taskRepository.findById(anyInt())).thenReturn(Optional.of(task));

        Task actualTask = taskWithTimezoneService.findById(1, "invalidTimezone").orElseThrow();

        assertThat(actualTask).usingRecursiveComparison().isEqualTo(makeTask(1, false));
    }

    @Test
    void whenGetTaskUpdateDtoByIdWithTimezoneThenReturnTaskUpdateDtoWithEditedCreatedToTimezone() {
        LocalDateTime now = LocalDateTime.now();
        when(taskRepository.findById(anyInt())).thenReturn(Optional.of(makeTask(1, false)));
        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        when(taskMapper.toTaskUpdateDto(taskArgumentCaptor.capture())).thenReturn(
                new TaskUpdateDto(
                        1,
                        "Title",
                        "Description",
                        Set.of(1, 2, 3),
                        1,
                        false,
                        now
                )
        );
        when(taskMapper.toTask(any(TaskUpdateDto.class), anySet(), any(Priority.class)))
                .thenReturn(makeTask(1, false));

        TaskUpdateDto actualTaskUpdateDto = taskWithTimezoneService
                .getTaskUpdateDtoById(1, "Europe/Moscow")
                .orElseThrow();
        List<Task> actualTasksValue = taskArgumentCaptor.getAllValues();

        assertThat(actualTasksValue.size()).isEqualTo(2);
        assertThat(actualTasksValue.get(0)).usingRecursiveComparison().isEqualTo(makeTask(1, false));
        assertThat(actualTasksValue.get(1)).usingRecursiveComparison().isEqualTo(Stream.of(
                makeTask(1, false)
        ).peek(tsk -> tsk.setCreated(tsk.getCreated().plusHours(3))).findFirst().get());
        assertThat(actualTaskUpdateDto).usingRecursiveComparison().isEqualTo(
                new TaskUpdateDto(
                        1,
                        "Title",
                        "Description",
                        Set.of(1, 2, 3),
                        1,
                        false,
                        now
                )
        );
    }

    @Test
    void whenGetTaskUpdateDtoByIdWithInvalidTimezoneThenReturnTaskUpdateDtoWithNotEditedCreated() {
        LocalDateTime now = LocalDateTime.now();
        when(taskRepository.findById(anyInt())).thenReturn(Optional.of(makeTask(1, false)));
        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        when(taskMapper.toTaskUpdateDto(taskArgumentCaptor.capture())).thenReturn(
                new TaskUpdateDto(
                        1,
                        "Title",
                        "Description",
                        Set.of(1, 2, 3),
                        1,
                        false,
                        now
                )
        );
        when(taskMapper.toTask(any(TaskUpdateDto.class), anySet(), any(Priority.class)))
                .thenReturn(makeTask(1, false));

        TaskUpdateDto actualTaskUpdateDto = taskWithTimezoneService
                .getTaskUpdateDtoById(1, "invalidTimezone")
                .orElseThrow();
        List<Task> actualTasksValue = taskArgumentCaptor.getAllValues();

        assertThat(actualTasksValue.size()).isEqualTo(1);
        assertThat(actualTasksValue.get(0)).usingRecursiveComparison().isEqualTo(makeTask(1, false));
        assertThat(actualTaskUpdateDto).usingRecursiveComparison().isEqualTo(
                new TaskUpdateDto(
                        1,
                        "Title",
                        "Description",
                        Set.of(1, 2, 3),
                        1,
                        false,
                        now
                )
        );
    }

    @Test
    void whenIdNotHaveInGetTaskUpdateDtoByIdThenReturnEmpty() {
        when(taskRepository.findById(anyInt())).thenReturn(Optional.empty());

        Optional<TaskUpdateDto> taskUpdateDtoById = taskWithTimezoneService
                .getTaskUpdateDtoById(1, "Europe/Moscow");

        assertThat(taskUpdateDtoById).isEmpty();
    }
}
