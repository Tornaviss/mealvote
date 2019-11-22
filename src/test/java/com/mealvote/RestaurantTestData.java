package com.mealvote;

import com.mealvote.model.restaurant.Restaurant;

import java.util.Comparator;

import static com.mealvote.model.AbstractBaseEntity.START_SEQ;

public class RestaurantTestData {
    public static final String[] IGNORED_FIELDS = {};

    public static final Comparator<Restaurant> COMPARATOR = Comparator.comparing(Restaurant::getName);

    public static final Integer DOMINOS_ID = START_SEQ + 2;
    public static final Integer VEGANO_ID = START_SEQ + 3;
    public static final Integer MAFIA_ID = START_SEQ + 4;

    public static final Restaurant DOMINOS = new Restaurant(DOMINOS_ID, "Dominos Pizza");
    public static final Restaurant VEGANO = new Restaurant(VEGANO_ID, "Vegano Huligano");
    public static final Restaurant MAFIA = new Restaurant(MAFIA_ID, "Mafia");

    public static Restaurant getCreated() {
        return new Restaurant("Genazvale&Khinkali");
    }

    public static Restaurant getUpdated() {
        return new Restaurant(VEGANO_ID, "Shaurma");
    }
}
