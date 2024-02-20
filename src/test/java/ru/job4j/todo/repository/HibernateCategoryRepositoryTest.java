package ru.job4j.todo.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.todo.configuration.SessionFactoryConfiguration;
import ru.job4j.todo.model.Category;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class HibernateCategoryRepositoryTest {
    private static SessionFactory sessionFactory;
    private static HibernateCategoryRepository categoryRepository;

    @BeforeAll
    static void beforeAll() {
        sessionFactory = new SessionFactoryConfiguration().createSessionFactory();
        categoryRepository = new HibernateCategoryRepository(new CrudRepository(sessionFactory));
        cleanCategoriesInDatabase();
    }

    @AfterEach
    void tearDown() {
        cleanCategoriesInDatabase();
    }

    private static void cleanCategoriesInDatabase() {
        categoryRepository.findAll().stream().map(Category::getId).forEach(categoryRepository::deleteById);
    }

    @AfterAll
    static void afterAll() {
        sessionFactory.close();
    }

    @Test
    void whenSaveCategoryThenReturnTrueAndSetIdInCategory() {
        Category category = new Category();
        category.setId(0);
        category.setName("Category");

        boolean isSave = categoryRepository.save(category);
        Category actualCategory = categoryRepository.findById(category.getId()).orElseThrow();

        assertThat(isSave).isTrue();
        assertThat(actualCategory).usingRecursiveComparison().isEqualTo(category);
    }

    @Test
    void whenSaveCategoryAndCrudRepositoryThrowRuntimeExceptionThenReturnFalse() {
        CrudRepository crudRepository = mock(CrudRepository.class);
        HibernateCategoryRepository categoryRepositoryMock = new HibernateCategoryRepository(crudRepository);
        doThrow(RuntimeException.class).when(crudRepository).run(any());

        Category category = new Category();
        boolean isSave = categoryRepositoryMock.save(category);

        assertThat(isSave).isFalse();
    }

    @Test
    void whenFindByIdAndCrudRepositoryThrowRuntimeExceptionThenReturnEmpty() {
        CrudRepository crudRepository = mock(CrudRepository.class);
        HibernateCategoryRepository repository = new HibernateCategoryRepository(crudRepository);
        doThrow(RuntimeException.class).when(crudRepository).optional(any(), any(), any());

        Optional<Category> actualCategoryOptional = repository.findById(1);

        assertThat(actualCategoryOptional).isEmpty();
    }

    @Test
    void whenFindAllAndCrudRepositoryThrowRuntimeExceptionThenReturnEmpty() {
        CrudRepository crudRepository = mock(CrudRepository.class);
        HibernateCategoryRepository repository = new HibernateCategoryRepository(crudRepository);
        doThrow(RuntimeException.class).when(crudRepository).query(any(), any());

        Collection<Category> actualCategories = repository.findAll();

        assertThat(actualCategories).isEmpty();
    }

    @Test
    void whenDeleteByIdCorrectThenReturnTrueAndEmpty() {
        Category category = new Category();
        category.setName("Category");
        categoryRepository.save(category);

        boolean isDeleted = categoryRepository.deleteById(category.getId());
        Optional<Category> actualCategory = categoryRepository.findById(category.getId());
        Collection<Category> actualCategories = categoryRepository.findAll();

        assertThat(isDeleted).isTrue();
        assertThat(actualCategory).isEmpty();
        assertThat(actualCategories).isEmpty();
    }

    @Test
    void whenDeleteByIdInEmptyDatabaseThenReturnFalse() {
        boolean isDeleted = categoryRepository.deleteById(1);
        Collection<Category> actualCategories = categoryRepository.findAll();

        assertThat(isDeleted).isFalse();
        assertThat(actualCategories).isEmpty();
    }

    @Test
    void whenDeleteByIdAndNotHaveIdInDatabaseThenReturnFalse() {
        Category category = new Category();
        category.setName("Category");
        categoryRepository.save(category);

        boolean isDeleted = categoryRepository.deleteById(10 + category.getId());
        Collection<Category> actualCategories = categoryRepository.findAll();

        assertThat(isDeleted).isFalse();
        assertThat(actualCategories).usingRecursiveComparison().isEqualTo(Set.of(category));
    }

    @Test
    void whenDeleteByIdAndCrudRepositoryThrowRuntimeExceptionThenReturnFalse() {
        CrudRepository crudRepository = mock(CrudRepository.class);
        HibernateCategoryRepository repository = new HibernateCategoryRepository(crudRepository);
        doThrow(RuntimeException.class).when(crudRepository).run(any(), any());

        boolean isDeleted = repository.deleteById(1);

        assertThat(isDeleted).isFalse();
    }

    @Test
    void whenFindAllByIdsThenReturnCollection() {
        List<Category> categories = IntStream.range(1, 4).boxed()
                .<Category>mapMulti((integer, categoryConsumer) -> {
                    Category category = new Category();
                    category.setName("Category " + integer);
                    categoryConsumer.accept(category);
                })
                .peek(categoryRepository::save)
                .toList();

        Collection<Category> actualCategories = categoryRepository
                .findAllByIds(Set.of(
                        categories.get(0).getId(), categories.get(2).getId()
                ));

        assertThat(actualCategories)
                .usingRecursiveComparison()
                .isEqualTo(Set.of(categories.get(0), categories.get(2)));
    }

    @Test
    void whenFindAllByIdsAndCrudRepositoryThrowRuntimeExceptionThenReturnEmpty() {
        CrudRepository crudRepository = mock(CrudRepository.class);
        HibernateCategoryRepository repository = new HibernateCategoryRepository(crudRepository);
        doThrow(RuntimeException.class).when(crudRepository).query(any(), any(), any());

        Collection<Category> actualCategories = repository.findAllByIds(Set.of(1, 2));

        assertThat(actualCategories).isEmpty();
    }
}