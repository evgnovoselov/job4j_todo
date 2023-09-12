package ru.job4j.todo.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.todo.configuration.SessionFactoryConfiguration;
import ru.job4j.todo.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.job4j.todo.util.UserUtil.makeUser;

class HibernateUserRepositoryTest {
    private static SessionFactory sessionFactory;
    private static HibernateUserRepository userRepository;

    @BeforeAll
    static void beforeAll() {
        sessionFactory = new SessionFactoryConfiguration().createSessionFactory();
        userRepository = new HibernateUserRepository(sessionFactory);
    }

    @AfterEach
    void tearDown() {
        userRepository.findAll().stream().map(User::getId).forEach(userRepository::deleteById);
    }

    @AfterAll
    static void afterAll() {
        sessionFactory.close();
    }

    @Test
    void whenSaveUserThenReturnTrueAndSetIdInUser() {
        User user = makeUser(7);
        user.setId(null);

        boolean isSave = userRepository.save(user);
        User actualUser = userRepository.findById(user.getId()).orElseThrow();

        assertThat(isSave).isTrue();
        assertThat(actualUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    void whenFindByIdHaveUserThenReturnOptionalUser() {
        User user = makeUser(7);
        user.setId(null);

        boolean isSave = userRepository.save(user);
        Optional<User> actualUser = userRepository.findById(user.getId());

        assertThat(isSave).isTrue();
        assertThat(actualUser).usingRecursiveComparison().isEqualTo(Optional.of(user));
    }

    @Test
    void whenFindByIdNotHaveUserThenReturnOptionalEmpty() {
        Optional<User> actualUser = userRepository.findById(7);

        assertThat(actualUser).isEmpty();
    }

    @Test
    void whenFindAllUserThenReturnCollections() {
        List<User> users = List.of(
                makeUser(1),
                makeUser(2),
                makeUser(3),
                makeUser(4)
        );
        users.forEach(user -> {
            user.setId(null);
            userRepository.save(user);
        });

        List<User> actualUsers = (List<User>) userRepository.findAll();

        assertThat(actualUsers).usingRecursiveComparison().isEqualTo(users);
    }

    @Test
    void whenFindAllAndNoHaveUserThenReturnCollectionsEmpty() {
        List<User> actualUsers = (List<User>) userRepository.findAll();

        assertThat(actualUsers).isEmpty();
    }

    @Test
    void whenDeleteUserCorrectThenReturnTrue() {
        User user = makeUser(7);
        user.setId(null);

        userRepository.save(user);
        boolean isDelete = userRepository.deleteById(user.getId());
        Collection<User> actualUsers = userRepository.findAll();

        assertThat(isDelete).isTrue();
        assertThat(actualUsers).isEmpty();
    }

    @Test
    void whenDeleteUserNotCorrectThenReturnFalse() {
        User user = makeUser(7);
        user.setId(null);

        userRepository.save(user);
        boolean isDelete = userRepository.deleteById(user.getId() + 10);
        Collection<User> actualUsers = userRepository.findAll();

        assertThat(isDelete).isFalse();
        assertThat(actualUsers).usingRecursiveComparison().isEqualTo(List.of(user));
    }

    @Test
    void whenSaveSeveralWithSameLoginThenSaveOnlyFirst() {
        List<User> users = List.of(
                makeUser(7),
                makeUser(8),
                makeUser(9)
        );
        users.forEach(user -> {
            user.setId(null);
            user.setLogin("login");
        });

        assertThat(userRepository.save(users.get(0))).isTrue();
        assertThat(userRepository.save(users.get(1))).isFalse();
        assertThat(userRepository.save(users.get(2))).isFalse();

        List<User> actualUsers = (List<User>) userRepository.findAll();

        assertThat(actualUsers).usingRecursiveComparison().isEqualTo(List.of(users.get(0)));
    }
}