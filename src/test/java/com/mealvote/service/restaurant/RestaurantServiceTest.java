package com.mealvote.service.restaurant;

import com.mealvote.VoteTestData;
import com.mealvote.model.restaurant.Restaurant;
import com.mealvote.util.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Collections;

import static com.mealvote.AssertionUtils.asSortedList;
import static com.mealvote.AssertionUtils.assertMatch;
import static com.mealvote.VoteTestData.USER_VOTE;
import static com.mealvote.RestaurantTestData.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(locations = {
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
class RestaurantServiceTest {

    @Autowired
    private RestaurantService service;

    @Test
    void getAll() {
        assertMatch(service.getAll(), asSortedList(COMPARATOR, DOMINOS, VEGANO, MAFIA), IGNORED_FIELDS);
    }

    @Test
    void get() {
        assertMatch(service.get(MAFIA_ID), MAFIA, IGNORED_FIELDS);
    }

    @Test
    void getNotFound() {
        assertThrows(NotFoundException.class,
                () -> service.get(1));
    }

    @Test
    void create() {
        Restaurant newRestaurant = getCreated();
        Restaurant created = service.create(newRestaurant);
        newRestaurant.setId(created.getId());
        assertMatch(created, newRestaurant, IGNORED_FIELDS);
        assertMatch(service.getAll(),
                asSortedList(COMPARATOR, newRestaurant, DOMINOS, VEGANO, MAFIA), IGNORED_FIELDS);
    }

    @Test
    void createNull() {
        assertThrows(IllegalArgumentException.class,
                () -> service.create(null));
    }

    @Test
    void createDuplicateName() {
        Restaurant newRestaurant = getCreated();
        newRestaurant.setName(DOMINOS.getName());
        assertThrows(DataIntegrityViolationException.class,
                () -> service.create(newRestaurant));
    }

    @Test
    void delete() {
        service.delete(DOMINOS_ID);
        assertMatch(service.getAll(), asSortedList(COMPARATOR, VEGANO, MAFIA), IGNORED_FIELDS);
        assertThrows(NotFoundException.class, () -> service.get(DOMINOS_ID));
    }

    @Test
    void deleteNotFound() {
        assertThrows(NotFoundException.class,
                () -> service.delete(1));
    }

    @Test
    void update() {
        Restaurant updated = getUpdated();
        service.update(updated, VEGANO_ID);
        assertMatch(service.getAll(), asSortedList(COMPARATOR, updated, DOMINOS, MAFIA), IGNORED_FIELDS);
    }

    @Test
    void updateNull() {
        assertThrows(IllegalArgumentException.class,
                () -> service.update(null, DOMINOS_ID));
    }

    @Test
    void updateNotFound(){
        Restaurant updated = getUpdated();
        updated.setId(1);
        assertThrows(NotFoundException.class, () -> service.update(updated, updated.getId()));
    }

    @Test
    void updateNameDuplicate() {
        Restaurant updated = getUpdated();
        updated.setName(MAFIA.getName());
        assertThrows(DataIntegrityViolationException.class,
                () -> service.update(updated, updated.getId()));
    }

    @Test
    void getWithVotes() {
        Restaurant actual = service.getWithVotes(DOMINOS_ID);
        assertMatch(actual.getVotes(), Collections.singletonList(USER_VOTE), VoteTestData.IGNORED_FIELDS);
    }

}