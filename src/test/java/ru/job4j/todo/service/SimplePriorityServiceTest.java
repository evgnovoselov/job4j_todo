package ru.job4j.todo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.todo.model.Priority;
import ru.job4j.todo.repository.PriorityRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.job4j.todo.util.PriorityUtil.makePriority;

class SimplePriorityServiceTest {
    private SimplePriorityService priorityService;
    private PriorityRepository priorityRepository;

    @BeforeEach
    void setUp() {
        priorityRepository = mock(PriorityRepository.class);
        priorityService = new SimplePriorityService(priorityRepository);
    }

    @Test
    void whenFindAllThenGetCollection() {
        List<Priority> priorities = List.of(
                makePriority(1),
                makePriority(2),
                makePriority(3)
        );
        when(priorityRepository.findAllByOrderByPosition()).thenReturn(priorities);

        Collection<Priority> actualPriorities = priorityService.findAllByOrderByPosition();

        assertThat(actualPriorities).isEqualTo(priorities);
    }

    @Test
    void whenFindByIdThenGet() {
        Priority priority = makePriority(1);
        when(priorityRepository.findById(anyInt())).thenReturn(Optional.of(priority));

        Priority actualPriority = priorityService.findById(1).orElseThrow();

        assertThat(actualPriority).usingRecursiveComparison().isEqualTo(priority);
    }
}
