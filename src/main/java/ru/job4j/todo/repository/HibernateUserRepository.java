package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HibernateUserRepository implements UserRepository {
    private final CrudRepository crudRepository;

    @Override
    public boolean save(User user) {
        try {
            crudRepository.run(session -> session.persist(user));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Collection<User> findAll() {
        try {
            return crudRepository.query("from User", User.class);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<User> findById(int id) {
        try {
            return crudRepository.optional(
                    "from User where id = :id",
                    User.class,
                    Map.of("id", id)
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByLoginAndPassword(String login, String password) {
        try {
            return crudRepository.optional(
                    "from User where login = :login and password = :password",
                    User.class,
                    Map.of(
                            "login", login,
                            "password", password
                    )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean deleteById(int id) {
        try {
            return crudRepository.run(
                    "delete from User where id = :id",
                    Map.of("id", id)
            );
        } catch (Exception e) {
            return false;
        }
    }
}
