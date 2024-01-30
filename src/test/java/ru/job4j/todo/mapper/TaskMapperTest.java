package ru.job4j.todo.mapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.job4j.todo.dto.TaskCreateDto;
import ru.job4j.todo.dto.TaskUpdateDto;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.util.TaskUtil;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TaskMapperTest {
    private static TaskMapper taskMapper;

    @BeforeAll
    static void beforeAll() {
        taskMapper = Mappers.getMapper(TaskMapper.class);
    }

    @Test
    void whenTaskNullToTaskCreateDtoThenReturnNull() {
        TaskCreateDto actualTaskCreateDto = taskMapper.toTaskCreateDto(null);

        assertThat(actualTaskCreateDto).isNull();
    }

    @Test
    void whenCleanTaskToTaskCreateDtoThenValidDto() {
        Task task = new Task();

        TaskCreateDto actualTaskCreateDto = taskMapper.toTaskCreateDto(task);

        assertThat(actualTaskCreateDto)
                .usingRecursiveComparison()
                .isEqualTo(new TaskCreateDto(null, null, null, null));
    }

    @Test
    void whenTaskNullToTaskUpdateDtoThenReturnNull() {
        TaskUpdateDto actualTaskUpdateDto = taskMapper.toTaskUpdateDto(null);

        assertThat(actualTaskUpdateDto).isNull();
    }

    @Test
    void whenCleanTaskToTaskUpdateDtoThenValidDto() {
        Task task = new Task();

        TaskUpdateDto actualTaskUpdateDto = taskMapper.toTaskUpdateDto(task);

        assertThat(actualTaskUpdateDto)
                .usingRecursiveComparison()
                .isEqualTo(new TaskUpdateDto(
                        0,
                        null,
                        null,
                        Set.of(),
                        null,
                        false,
                        null
                ));
    }

    @Test
    void whenTaskToTaskUpdateDtoThenValidDto() {
        Task task = TaskUtil.makeTask(1, true);

        TaskUpdateDto actualTaskUpdateDto = taskMapper.toTaskUpdateDto(task);

        assertThat(actualTaskUpdateDto)
                .usingRecursiveComparison()
                .isEqualTo(new TaskUpdateDto(
                        1,
                        "Task title 1",
                        "Task description 1",
                        Set.of(1, 2),
                        1,
                        true,
                        task.getCreated()
                ));
    }

    @Test
    void whenTaskCreateDtoToTaskThenValidTask() {
        TaskCreateDto taskCreateDto = new TaskCreateDto(
                "Task title 1",
                Set.of(1, 2),
                1,
                "Task description 1"
        );
        Task task = TaskUtil.makeTask(1, false);
        task.setId(null);
        task.setCreated(null);
        Task actualTask = taskMapper.toTask(taskCreateDto, task.getCategories(), task.getPriority());

        assertThat(actualTask).usingRecursiveComparison()
                .isEqualTo(task);
    }

    @Test
    void whenTaskCreateDtoNullToTaskThenNull() {
        Task actualTask = taskMapper.toTask((TaskCreateDto) null, null, null);

        assertThat(actualTask).isNull();
    }

    @Test
    void whenTaskUpdateDtoToTaskThenValidTask() {
        TaskUpdateDto taskUpdateDto = new TaskUpdateDto(
                1,
                "Task title 1",
                "Task description 1",
                Set.of(1, 2),
                1,
                false,
                TaskUtil.makeTask(1, false).getCreated()
        );
        Task task = TaskUtil.makeTask(1, false);
        Task actualTask = taskMapper.toTask(taskUpdateDto, task.getCategories(), task.getPriority());

        assertThat(actualTask).usingRecursiveComparison()
                .isEqualTo(task);
    }

    @Test
    void whenTaskUpdateDtoNullThenNull() {
        Task actualTask = taskMapper.toTask((TaskUpdateDto) null, null, null);

        assertThat(actualTask).isNull();
    }
}