package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

@Repository
@AllArgsConstructor
public class HibernateTaskRepository implements TaskRepository {
    private final SessionFactory sf;

    @Override
    public boolean save(Task task) {
        return fromTransaction(session -> {
            session.persist(task);
            return true;
        }) != null;
    }

    @Override
    public boolean update(Task task) {
        return fromTransaction(session -> {
            session.merge(task);
            return true;
        }) != null;
    }

    @Override
    public Collection<Task> findAllByOrderByCreatedDesc() {
        return fromTransaction(session -> session.createQuery("from Task order by created desc", Task.class).list());
    }

    @Override
    public Collection<Task> findAllByDoneTrueOrderByCreatedDesc() {
        return fromTransaction(session -> session.createQuery("from Task where done = true order by created desc", Task.class).list());
    }

    @Override
    public Collection<Task> findAllByDoneFalseOrderByCreatedDesc() {
        return fromTransaction(session -> session.createQuery("from Task where done = false order by created desc", Task.class).list());
    }

    @Override
    public Optional<Task> findById(int id) {
        return Optional.ofNullable(fromTransaction(session -> session.get(Task.class, id)));
    }

    @Override
    public boolean deleteById(int id) {
        return fromTransaction(session -> {
            Task task = new Task();
            task.setId(id);
            session.remove(task);
            return true;
        }) != null;
    }

    @Override
    public boolean setStatusById(int id, boolean done) {
        return fromTransaction(session -> {
            session.createMutationQuery("update Task set done = :done where id = :id")
                    .setParameter("done", done)
                    .setParameter("id", id)
                    .executeUpdate();
            return true;
        }) != null;
    }

    private <R> R fromTransaction(Function<Session, R> action) {
        R result = null;
        Session session = sf.openSession();
        try (session) {
            session.beginTransaction();
            R apply = action.apply(session);
            session.getTransaction().commit();
            result = apply;
        } catch (Exception e) {
            session.getTransaction().rollback();
        }
        return result;
    }
}
