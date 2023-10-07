package ru.job4j.todo.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.todo.model.Priority;
import ru.job4j.todo.repository.PriorityRepository;

import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SimplePriorityService implements PriorityService {
    private final PriorityRepository priorityRepository;

    @Override
    public Optional<Priority> findById(int id) {
        return priorityRepository.findById(id);
    }

    @Override
    public Collection<Priority> findAllByOrderByPosition() {
        return priorityRepository.findAllByOrderByPosition();
    }
}
