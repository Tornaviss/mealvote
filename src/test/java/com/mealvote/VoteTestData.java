package com.mealvote;

import com.mealvote.model.user.Vote;

import java.time.LocalDateTime;
import java.util.Comparator;

import static com.mealvote.RestaurantTestData.DOMINOS;
import static com.mealvote.UserTestData.USER;
import static com.mealvote.UserTestData.USER_ID;

public class VoteTestData {

    public static final String[] IGNORED_FIELDS = {"dateTime", "user", "restaurant"};

    public static final Comparator<Vote> VOTE_COMPARATOR = Comparator.comparing(Vote::getDateTime).reversed();

    public static final Vote USER_VOTE = new Vote(USER_ID, USER, DOMINOS, LocalDateTime.now());
}
