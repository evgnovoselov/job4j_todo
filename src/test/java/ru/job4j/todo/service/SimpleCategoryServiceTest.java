package ru.job4j.todo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.todo.model.Category;
import ru.job4j.todo.repository.CategoryRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimpleCategoryServiceTest {
    private CategoryRepository categoryRepository;
    private SimpleCategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        categoryService = new SimpleCategoryService(categoryRepository);
    }

    @Test
    void whenFindAllThenReturnAll() {
        List<Category> categories = IntStream.range(1, 3).boxed().<Category>mapMulti((id, categoryConsumer) -> {
            Category category = new Category();
            category.setId(id);
            category.setName("Category " + id);
            categoryConsumer.accept(category);
        }).toList();
        when(categoryRepository.findAll()).thenReturn(categories);

        Collection<Category> actualCategories = categoryService.findAll();

        assertThat(actualCategories).isEqualTo(categories);
    }

    @Test
    void whenFindByIdThenReturn() {
        Category category = new Category();
        category.setId(1);
        category.setName("Category");
        when(categoryRepository.findById(anyInt())).thenReturn(Optional.of(category));

        Optional<Category> actualCategory = categoryService.findById(1);

        assertThat(actualCategory).isPresent();
        assertThat(actualCategory.get()).usingRecursiveComparison().isEqualTo(category);
    }
}