package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {

    public static final int NOT_FOUND = 10;
    public static final int MEAL1_ID = START_SEQ + 3;

    public static final Meal meal1 = new Meal(MEAL1_ID,     LocalDateTime.of(2020, 1, 30, 10, 0), "Завтрак", 500);
    public static final Meal meal2 = new Meal(MEAL1_ID + 1, LocalDateTime.of(2020, 1, 30, 13, 0), "Обед", 1000);
    public static final Meal meal3 = new Meal(MEAL1_ID + 2, LocalDateTime.of(2020, 1, 30, 20, 0), "Ужин", 500);
    public static final Meal meal4 = new Meal(MEAL1_ID + 3, LocalDateTime.of(2020, 1, 31,  0, 0), "Еда на граничное значение", 100);
    public static final Meal meal5 = new Meal(MEAL1_ID + 4, LocalDateTime.of(2020, 1, 31, 10, 0), "Завтрак", 1000);
    public static final Meal meal6 = new Meal(MEAL1_ID + 5, LocalDateTime.of(2020, 1, 31, 13, 0), "Обед", 500);
    public static final Meal meal7 = new Meal(MEAL1_ID + 6, LocalDateTime.of(2020, 1, 31, 20, 0), "Ужин", 410);

    public static final Meal adminMeal1 = new Meal(MEAL1_ID + 7, LocalDateTime.of(2020, 1, 30, 10, 0), "Завтрак Админа", 500);
    public static final Meal adminMeal2 = new Meal(MEAL1_ID + 8, LocalDateTime.of(2020, 1, 31, 13, 0), "Обед Админа", 100);
    public static final Meal adminMeal3 = new Meal(MEAL1_ID + 9, LocalDateTime.of(2020, 1, 31, 20, 0), "Ужин Админа", 410);

    public static Meal getNew() {
        return new Meal(null, LocalDateTime.of(2020, 2, 1, 10, 0), "Новая еда", 300);
    }

    public static Meal getUpdated() {
        return new Meal(MEAL1_ID, LocalDateTime.of(2020, 1, 30, 10, 0), "Обновлённый завтрак", 700);
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, List<Meal> expected) {
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().isEqualTo(expected);
    }
}