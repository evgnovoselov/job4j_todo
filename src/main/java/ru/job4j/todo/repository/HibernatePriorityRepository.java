package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Priority;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
@Slf4j
public class HibernatePriorityRepository implements PriorityRepository {
    private final CrudRepository crudRepository;

    @Override
    public Optional<Priority> findById(int id) {
        try {
            return crudRepository.optional(
                    "from Priority where id = :id",
                    Priority.class,
                    Map.of("id", id)
            );
        } catch (Exception e) {
            log.error("Error find priority by id = {}", id);
        }
        return Optional.empty();
    }

    @Override
    public Collection<Priority> findAllByOrderByPosition() {
        try {
            return crudRepository.query("from Priority order by position", Priority.class);
        } catch (Exception e) {
            log.error("Error find all priorities tasks by position");
        }
        return Collections.emptyList();
    }
}
