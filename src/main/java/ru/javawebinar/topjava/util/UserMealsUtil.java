package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );
        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);
        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
        System.out.println(filteredByCyclesOptional2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
        System.out.println(filteredByStreamsOptional2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime,
                                                            LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesPerDayMap = new HashMap<>();
        for (UserMeal meal : meals) {
            caloriesPerDayMap.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
        }
        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal meal : meals) {
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                boolean excess = caloriesPerDayMap.get(meal.getDateTime().toLocalDate()) > caloriesPerDay;
                result.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess));
            }
        }
        return result;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime,
                                                             LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesPerDayMap = meals.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getDateTime().toLocalDate(),
                        Collectors.summingInt(UserMeal::getCalories)
                ));
        return meals.stream()
                .filter(m -> TimeUtil.isBetweenHalfOpen(m.getDateTime().toLocalTime(), startTime, endTime))
                .map(m -> new UserMealWithExcess(
                        m.getDateTime(),
                        m.getDescription(),
                        m.getCalories(),
                        caloriesPerDayMap.get(m.getDateTime().toLocalDate()) > caloriesPerDay
                ))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByCyclesOptional2(List<UserMeal> meals, LocalTime startTime,
                                                                     LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, AtomicInteger> caloriesMap = new HashMap<>();
        Map<LocalDate, AtomicBoolean> excessMap = new HashMap<>();
        List<UserMealWithExcess> result = new ArrayList<>();

        for (UserMeal meal : meals) {
            LocalDate date = meal.getDateTime().toLocalDate();
            AtomicInteger dailyCalories = caloriesMap.computeIfAbsent(date, d -> new AtomicInteger());
            AtomicBoolean excess = excessMap.computeIfAbsent(date, d -> new AtomicBoolean());
            excess.set(dailyCalories.addAndGet(meal.getCalories()) > caloriesPerDay);
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                result.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(),
                        meal.getCalories(), excess));
            }
        }
        return result;
    }

    public static List<UserMealWithExcess> filteredByStreamsOptional2(List<UserMeal> meals, LocalTime startTime,
                                                                      LocalTime endTime, int caloriesPerDay) {
        class Accumulator {
            final Map<LocalDate, AtomicInteger> caloriesMap = new HashMap<>();
            final Map<LocalDate, AtomicBoolean> excessMap = new HashMap<>();
            final List<UserMealWithExcess> result = new ArrayList<>();

            void accept(UserMeal meal) {
                LocalDate date = meal.getDateTime().toLocalDate();
                AtomicInteger dailyCalories = caloriesMap.computeIfAbsent(date, d -> new AtomicInteger());
                AtomicBoolean excess = excessMap.computeIfAbsent(date, d -> new AtomicBoolean());
                excess.set(dailyCalories.addAndGet(meal.getCalories()) > caloriesPerDay);
                if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                    result.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(),
                            meal.getCalories(), excess));
                }
            }

            List<UserMealWithExcess> finish() {
                return result;
            }
        }

        return meals.stream().collect(Collector.of(
                Accumulator::new,
                Accumulator::accept,
                (c1, c2) -> { throw new UnsupportedOperationException("Parallel stream not supported"); },
                Accumulator::finish
        ));
    }
}