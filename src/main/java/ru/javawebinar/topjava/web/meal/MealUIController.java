package ru.javawebinar.topjava.web.meal;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/meals")
public class MealUIController extends AbstractMealController {

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("meals", super.getAll());
        return "meals";
    }

    @PostMapping
    @ResponseBody
    public Meal create(@RequestParam String dateTime,
                       @RequestParam String description,
                       @RequestParam int calories) {
        return super.create(new Meal(LocalDateTime.parse(dateTime), description, calories));
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }

    @GetMapping("/filter")
    @ResponseBody
    public List<MealTo> getBetween(
            @RequestParam @Nullable LocalDate startDate,
            @RequestParam @Nullable LocalTime startTime,
            @RequestParam @Nullable LocalDate endDate,
            @RequestParam @Nullable LocalTime endTime) {
        return super.getBetween(startDate, startTime, endDate, endTime);
    }
}