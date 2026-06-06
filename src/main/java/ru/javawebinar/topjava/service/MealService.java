package ru.javawebinar.topjava.service;

import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.List;

@Service
public class MealService {

    private final MealRepository repository;

    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Meal get(int id, int userId) {
        Meal meal = repository.get(id, userId);
        if (meal == null) {
            throw new NotFoundException("Meal id=" + id + " not found for userId=" + userId);
        }
        return meal;
    }

    public void delete(int id, int userId) {
        if (!repository.delete(id, userId)) {
            throw new NotFoundException("Meal id=" + id + " not found for userId=" + userId);
        }
    }

    public List<Meal> getAll(int userId) {
        return repository.getAll(userId);
    }

    public Meal update(Meal meal, int userId) {
        Meal updated = repository.save(meal, userId);
        if (updated == null) {
            throw new NotFoundException("Meal id=" + meal.getId() + " not found for userId=" + userId);
        }
        return updated;
    }

    public Meal create(Meal meal, int userId) {
        return repository.save(meal, userId);
    }
}