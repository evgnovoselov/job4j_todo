package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.User;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

@Repository
@AllArgsConstructor
public class HibernateUserRepository implements UserRepository {
    private final SessionFactory sf;

    @Override
    public boolean save(User user) {
        return fromTransaction(session -> {
            session.persist(user);
            return true;
        }).orElse(false);
    }

    @Override
    public Collection<User> findAll() {
        return fromTransaction(session -> session.createQuery("from User", User.class)
                .list()).orElse(Collections.emptyList());
    }

    @Override
    public Optional<User> findById(int id) {
        return fromTransaction(session -> session.get(User.class, id));
    }

    @Override
    public boolean deleteById(int id) {
        return fromTransaction(session -> {
            User user = new User();
            user.setId(id);
            session.remove(user);
            return true;
        }).orElse(false);
    }

    private <R> Optional<R> fromTransaction(Function<Session, R> action) {
        Optional<R> result = Optional.empty();
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            R apply = action.apply(session);
            session.getTransaction().commit();
            result = Optional.ofNullable(apply);
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return result;
    }
}
