package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.repository.InMemoryMealRepository;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);

    private MealRepository repository;

    @Override
    public void init() {
        repository = new InMemoryMealRepository();
        MealsUtil.meals.forEach(repository::save);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            repository.delete(id);
            log.debug("delete meal id={}", id);
            response.sendRedirect("meals");

        } else if ("create".equals(action) || "update".equals(action)) {
            Meal meal = "create".equals(action)
                    ? new Meal(LocalDateTime.now(), "", 1000)
                    : repository.get(Integer.parseInt(request.getParameter("id")));
            request.setAttribute("meal", meal);
            log.debug("forward to mealForm");
            request.getRequestDispatcher("/mealForm.jsp").forward(request, response);

        } else {
            log.debug("forward to meals");
            List<MealTo> meals = MealsUtil.filteredByStreams(repository.getAll(), MealsUtil.CALORIES_PER_DAY);
            request.setAttribute("meals", meals);
            request.getRequestDispatcher("/meals.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String id = request.getParameter("id");
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories"))
        );
        if (id != null && !id.isEmpty()) {
            meal.setId(Integer.parseInt(id));
        }
        repository.save(meal);
        log.debug("save meal {}", meal);
        response.sendRedirect("meals");
    }
}