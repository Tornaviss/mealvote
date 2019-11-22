package com.mealvote;

import com.mealvote.model.user.Choice;

import java.time.LocalDateTime;
import java.util.Comparator;

import static com.mealvote.RestaurantTestData.DOMINOS;
import static com.mealvote.RestaurantTestData.VEGANO;
import static com.mealvote.UserTestData.USER;
import static com.mealvote.UserTestData.USER_ID;

public class ChoiceTestData {

    public static final String[] IGNORED_FIELDS = {"dateTime", "user", "restaurant"};

    public static final Comparator<Choice> CHOICE_COMPARATOR = Comparator.comparing(Choice::getDateTime).reversed();

    public static final Choice USER_CHOICE = new Choice(USER_ID, USER, DOMINOS, LocalDateTime.now());
}
