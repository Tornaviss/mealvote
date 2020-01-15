package com.mealvote.util;

import com.mealvote.model.restaurant.Dish;
import com.mealvote.model.restaurant.Menu;
import com.mealvote.util.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MenuUtil {

    public static void refreshDishes(Menu menu, List<Dish> dishes) {
        Map<Integer, Dish> completionMap = menu.getDishes().stream()
                        .collect(Collectors.toMap(Dish::getId, Function.identity()));
        List<Dish> newDishes = new ArrayList<>(dishes.size());

        for (Dish updated : dishes) {
            if (updated.getId() == null) {
                updated.setMenu(menu);
                newDishes.add(updated);
                continue;
            }
            boolean found = findAndCopyState(updated, menu.getDishes());
            if (found) {
                completionMap.remove(updated.getId());
            } else {
                throw new NotFoundException("Dish with id = " + updated.getId());
            }
        }

        completionMap.forEach((k, v) -> menu.getDishes().remove(v));
        menu.getDishes().addAll(newDishes);
    }

    private static boolean findAndCopyState(Dish toFound, List<Dish> dishes) {
        for (Dish dish : dishes) {
            if (toFound.getId().equals(dish.getId())) {
                dish.setName(toFound.getName());
                dish.setPrice(toFound.getPrice());
                return true;
            }
        }
        return false;
    }
}
