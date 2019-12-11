package com.mealvote;

import com.mealvote.model.restaurant.Dish;
import com.mealvote.model.restaurant.Menu;

import java.util.Comparator;
import java.util.List;

import static com.mealvote.AssertionUtils.asSortedList;
import static com.mealvote.RestaurantTestData.*;
import static com.mealvote.model.AbstractBaseEntity.START_SEQ;

public class MenuTestData {
    public static final String[] MENU_IGNORED_FIELDS = {"restaurant", "dishes"};
    public static final String[] DISH_IGNORED_FIELDS = {"menu"};

    public static final Comparator<Menu> MENU_COMPARATOR = Comparator.comparing(Menu::getDate).reversed()
            .thenComparing(Menu::getId);
    public static final Comparator<Dish> DISH_COMPARATOR = Comparator.comparing(Dish::getName);

    public static final Integer DOMINOS_DISH1_ID = START_SEQ + 5;
    public static final Integer DOMINOS_DISH2_ID = START_SEQ + 6;
    public static final Integer VEGANO_DISH1_ID = START_SEQ + 7;
    public static final Integer VEGANO_DISH2_ID = START_SEQ + 8;

    public static final Dish DOMINOS_DISH1 = new Dish(DOMINOS_DISH1_ID, "big ass pizza", 1000, null);
    public static final Dish DOMINOS_DISH2 = new Dish(DOMINOS_DISH2_ID, "pepsi", 500, null);
    public static final Dish VEGANO_DISH1 = new Dish(VEGANO_DISH1_ID, "falafel with roaches", 10000, null);
    public static final Dish VEGANO_DISH2 = new Dish(VEGANO_DISH2_ID, "compotik", 500, null);

    public static final Menu DOMINOS_MENU = new Menu(DOMINOS_ID, DOMINOS,
            asSortedList(DISH_COMPARATOR, DOMINOS_DISH1, DOMINOS_DISH2));
    public static final Menu VEGANO_MENU = new Menu(VEGANO_ID, VEGANO,
            asSortedList(DISH_COMPARATOR, VEGANO_DISH2, VEGANO_DISH1));

    static {
        DOMINOS_DISH1.setMenu(DOMINOS_MENU);
        DOMINOS_DISH2.setMenu(DOMINOS_MENU);
        VEGANO_DISH1.setMenu(VEGANO_MENU);
        VEGANO_DISH2.setMenu(VEGANO_MENU);
    }

    public static Menu getCreated() {
        return new Menu(List.of(
                new Dish("createdDish1", 1000),
                new Dish("createdDish2", 5000)));
    }

    public static Menu getUpdated() {
        return new Menu(VEGANO_ID, VEGANO,
                asSortedList(DISH_COMPARATOR, new Dish("tortik", 700), new Dish("kartoshechka", 100)));
    }

    public static Dish getCreatedDish() {
        return new Dish("Dark beer", 400);
    }

    public static Dish getUpdatedDish() {
        return new Dish(VEGANO_DISH1_ID, "falafel", 10000, VEGANO_MENU);
    }
}
