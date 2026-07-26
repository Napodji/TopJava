package ru.javawebinar.topjava.service.datajpa;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.AbstractUserServiceTest;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.List;

import static ru.javawebinar.topjava.MealTestData.MEAL_MATCHER;
import static ru.javawebinar.topjava.MealTestData.adminMeal1;
import static ru.javawebinar.topjava.MealTestData.adminMeal2;
import static ru.javawebinar.topjava.Profiles.DATAJPA;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_MATCHER;

@ActiveProfiles(DATAJPA)
public class DataJpaUserServiceTest extends AbstractUserServiceTest {

    @Test
    public void getWithMeals() {
        User user = service.getWithMeals(ADMIN_ID);

        Assert.assertEquals(UserTestData.admin.id(), user.id());
        Assert.assertEquals(UserTestData.admin.getName(), user.getName());
        Assert.assertEquals(UserTestData.admin.getEmail(), user.getEmail());
        Assert.assertEquals(UserTestData.admin.getPassword(), user.getPassword());
        Assert.assertEquals(UserTestData.admin.isEnabled(), user.isEnabled());
        Assert.assertEquals(UserTestData.admin.getCaloriesPerDay(), user.getCaloriesPerDay());

        MEAL_MATCHER.assertMatch(user.getMeals(), List.of(adminMeal2, adminMeal1));
    }

    @Test
    public void getWithMealsNotFound() {
        Assert.assertThrows(NotFoundException.class,
                () -> service.getWithMeals(UserTestData.NOT_FOUND));
    }
}