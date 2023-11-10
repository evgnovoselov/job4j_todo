package ru.job4j.todo.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.todo.configuration.SessionFactoryConfiguration;
import ru.job4j.todo.model.Priority;
import ru.job4j.todo.util.PriorityUtil;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static ru.job4j.todo.util.PriorityUtil.*;

class HibernatePriorityRepositoryTest {
    private static SessionFactory sessionFactory;
    private static HibernatePriorityRepository priorityRepository;

    @BeforeAll
    static void beforeAll() {
        sessionFactory = new SessionFactoryConfiguration().createSessionFactory();
        priorityRepository = new HibernatePriorityRepository(new CrudRepository(sessionFactory));
        cleanPrioritiesInDatabase();
    }

    @AfterEach
    void tearDown() {
        cleanPrioritiesInDatabase();
    }

    private static void cleanPrioritiesInDatabase() {
        priorityRepository.findAllByOrderByPosition().stream().map(Priority::getId).forEach(priorityRepository::deleteById);
    }

    @AfterAll
    static void afterAll() {
        sessionFactory.close();
    }

    @Test
    void whenSavePriorityThenReturnTrueAndSetIdInPriority() {
        Priority priority = makePriority(1);
        priority.setId(0);

        boolean isSave = priorityRepository.save(priority);
        Priority actualPriority = priorityRepository.findById(priority.getId()).orElseThrow();

        assertThat(isSave).isTrue();
        assertThat(actualPriority).usingRecursiveComparison().isEqualTo(priority);
    }

    @Test
    void whenSavePriorityAndCrudRepositoryThrowRuntimeExceptionThenReturnFalse() {
        CrudRepository crudRepository = mock(CrudRepository.class);
        HibernatePriorityRepository priorityRepositoryMock = new HibernatePriorityRepository(crudRepository);
        doThrow(RuntimeException.class).when(crudRepository).run(any());

        Priority priority = new Priority();
        boolean isSave = priorityRepositoryMock.save(priority);

        assertThat(isSave).isFalse();
    }

    @Test
    void whenFindByIdAndCrudRepositoryThrowRuntimeExceptionThenReturnEmpty() {
        CrudRepository crudRepository = mock(CrudRepository.class);
        HibernatePriorityRepository priorityRepositoryMock = new HibernatePriorityRepository(crudRepository);
        doThrow(RuntimeException.class).when(crudRepository).optional(any(), any(), any());

        Optional<Priority> actualPriorityOptional = priorityRepositoryMock.findById(1);

        assertThat(actualPriorityOptional).isEmpty();
    }

    @Test
    void whenFindAllByOrderByPositionThenGetPrioritiesOrderByPosition() {
        List<Priority> priorities = List.of(
                makePriority(3),
                makePriority(1),
                makePriority(2)
        );
        priorities.forEach(priority -> {
            priority.setId(0);
            priorityRepository.save(priority);
        });

        Collection<Priority> actualPrioritiesOrderByPosition = priorityRepository.findAllByOrderByPosition();

        assertThat(actualPrioritiesOrderByPosition).usingRecursiveComparison().isEqualTo(
                List.of(priorities.get(1), priorities.get(2), priorities.get(0))
        );
    }

    @Test
    void whenFindAllByOrderByPositionAndCrudRepositoryThrowRuntimeExceptionThenReturnEmpty() {
        CrudRepository crudRepository = mock(CrudRepository.class);
        HibernatePriorityRepository priorityRepositoryMock = new HibernatePriorityRepository(crudRepository);
        doThrow(RuntimeException.class).when(crudRepository).query(any(), any());

        Collection<Priority> actualPriority = priorityRepositoryMock.findAllByOrderByPosition();

        assertThat(actualPriority).isEmpty();
    }

    @Test
    void whenDeleteByIdCorrectThenReturnTrueAndEmpty() {
        Priority priority = makePriority(1);
        priority.setId(0);
        priorityRepository.save(priority);

        boolean isDeleted = priorityRepository.deleteById(priority.getId());
        Collection<Priority> actualPriorities = priorityRepository.findAllByOrderByPosition();

        assertThat(isDeleted).isTrue();
        assertThat(actualPriorities).isEmpty();
    }

    @Test
    void whenDeleteByIdInEmptyDatabaseThenReturnFalse() {
        boolean isDeleted = priorityRepository.deleteById(1);
        Collection<Priority> actualPriorities = priorityRepository.findAllByOrderByPosition();

        assertThat(isDeleted).isFalse();
        assertThat(actualPriorities).isEmpty();
    }

    @Test
    void whenDeleteByIdAndNotHaveIdInDatabaseThenReturnFalse() {
        Priority priority = makePriority(1);
        priority.setId(0);
        priorityRepository.save(priority);

        boolean isDeleted = priorityRepository.deleteById(10 + priority.getId());
        Collection<Priority> actualPriorities = priorityRepository.findAllByOrderByPosition();

        assertThat(isDeleted).isFalse();
        assertThat(actualPriorities).usingRecursiveComparison().isEqualTo(List.of(priority));
    }

    @Test
    void whenDeleteByIdAndCrudRepositoryThrowRuntimeExceptionThenReturnFalse() {
        CrudRepository crudRepository = mock(CrudRepository.class);
        HibernatePriorityRepository priorityRepositoryMock = new HibernatePriorityRepository(crudRepository);
        doThrow(RuntimeException.class).when(crudRepository).run(any(), any());

        boolean isDeleted = priorityRepositoryMock.deleteById(1);

        assertThat(isDeleted).isFalse();
    }
}
