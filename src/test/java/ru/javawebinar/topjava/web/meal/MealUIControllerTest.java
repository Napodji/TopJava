package ru.javawebinar.topjava.web.meal;

import org.junit.jupiter.api.Test;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.SecurityUtil;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.javawebinar.topjava.MealTestData.meals;
import static ru.javawebinar.topjava.util.MealsUtil.getTos;

class MealUIControllerTest extends AbstractControllerTest {

    @Test
    void getAll() throws Exception {
        perform(get("/meals"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("meals"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/meals.jsp"))
                .andExpect(model().attribute("meals", getTos(meals, SecurityUtil.authUserCaloriesPerDay())));
    }
}