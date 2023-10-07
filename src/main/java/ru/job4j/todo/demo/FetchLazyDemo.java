package ru.job4j.todo.demo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.todo.model.Task;

import java.util.List;

public class FetchLazyDemo {
    public static void main(String[] args) {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        try {
            SessionFactory sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            List<Task> tasks = listOf("from Task t join fetch t.priority", Task.class, sessionFactory);
            for (Task task : tasks) {
                System.out.println(task.getPriority());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    private static <T> List<T> listOf(String query, Class<T> model, SessionFactory sessionFactory) {
        Session session = sessionFactory.openSession();
        List<T> result = session.createQuery(query, model).getResultList();
        session.close();
        return result;
    }
}
