package ru.job4j.todo.mapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

// TODO add tests
@Disabled
class TaskMapperTest {
    private static TaskMapper taskMapper;

    @BeforeAll
    static void beforeAll() {
        taskMapper = Mappers.getMapper(TaskMapper.class);
    }

    @Test
    void whenMapperThenMapper() {

    }
}