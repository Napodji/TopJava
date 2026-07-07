package ru.javawebinar.topjava.service.datajpa;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.AbstractUserServiceTest;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static ru.javawebinar.topjava.MealTestData.MEAL_MATCHER;
import static ru.javawebinar.topjava.MealTestData.meals;
import static ru.javawebinar.topjava.UserTestData.GUEST_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.UserTestData.USER_MATCHER;
import static ru.javawebinar.topjava.UserTestData.guest;
import static ru.javawebinar.topjava.UserTestData.user;

@ActiveProfiles(Profiles.DATAJPA)
public class DataJpaUserServiceTest extends AbstractUserServiceTest {

    @Test
    public void getWithMeals() {
        User actual = service.getWithMeals(USER_ID);

        USER_MATCHER.assertMatch(actual, user);
        assertEquals(meals.size(), actual.getMeals().size());
        MEAL_MATCHER.assertMatch(actual.getMeals(), meals);
    }

    @Test
    public void getWithMealsNoMeals() {
        User actual = service.getWithMeals(GUEST_ID);

        USER_MATCHER.assertMatch(actual, guest);
        assertEquals(Collections.emptyList(), actual.getMeals());
    }
}