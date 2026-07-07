package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.util.List;

@Repository
public class DataJpaUserRepository implements UserRepository {

    private static final Sort SORT_NAME_EMAIL = Sort.by(Sort.Direction.ASC, "name", "email");

    private final CrudUserRepository crudRepository;
    private final CrudMealRepository crudMealRepository;

    public DataJpaUserRepository(CrudUserRepository crudRepository, CrudMealRepository crudMealRepository) {
        this.crudRepository = crudRepository;
        this.crudMealRepository = crudMealRepository;
    }

    @Override
    public User save(User user) {
        return crudRepository.save(user);
    }

    @Override
    public boolean delete(int id) {
        return crudRepository.delete(id) != 0;
    }

    @Override
    public User get(int id) {
        return crudRepository.findById(id).orElse(null);
    }

    @Override
    public User getByEmail(String email) {
        return crudRepository.getByEmail(email);
    }

    @Override
    public List<User> getAll() {
        return crudRepository.findAll(SORT_NAME_EMAIL);
    }

    public User getUserWithMeals(int userId) {
        List<Meal> meals = crudMealRepository.getAllWithUser(userId);
        User user = meals.isEmpty()
                ? crudRepository.findById(userId).orElse(null)
                : meals.get(0).getUser();
        if (user != null) {
            user.setMeals(meals);
        }
        return user;
    }
}