package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.User;

import java.util.List;

public interface UserRepository {
    // null if updated user does not exist
    User save(User user);

    // false if user does not exist
    boolean delete(int id);

    // null if user does not exist
    User get(int id);

    // null if user does not exist
    User getByEmail(String email);

    List<User> getAll();

    default User getWithMeals(int id) {
        throw new UnsupportedOperationException();
    }
}