package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Category;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
@Slf4j
public class HibernateCategoryRepository implements CategoryRepository {
    private final CrudRepository crudRepository;

    @Override
    public Collection<Category> findAll() {
        try {
            return crudRepository.query("from Category", Category.class);
        } catch (Exception e) {
            log.error("Error find all categories");
        }
        return Collections.emptyList();
    }

    @Override
    public Optional<Category> findById(int id) {
        try {
            return crudRepository.optional(
                    "from Category where id = :id",
                    Category.class,
                    Map.of("id", id)
            );
        } catch (Exception e) {
            log.error("Error find category by id = {}", id);
        }
        return Optional.empty();
    }
}