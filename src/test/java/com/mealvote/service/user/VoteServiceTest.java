package com.mealvote.service.user;

import com.mealvote.RestaurantTestData;
import com.mealvote.model.user.Vote;
import com.mealvote.util.exception.TemporaryUnavailableOperationException;
import com.mealvote.util.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalTime;

import static com.mealvote.AssertionUtils.assertMatch;
import static com.mealvote.VoteTestData.IGNORED_FIELDS;
import static com.mealvote.VoteTestData.USER_VOTE;
import static com.mealvote.RestaurantTestData.VEGANO;
import static com.mealvote.RestaurantTestData.VEGANO_ID;
import static com.mealvote.UserTestData.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringJUnitConfig(locations = {
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
class VoteServiceTest {

    @Autowired
    private VoteService service;

    @Test
    void get() {
        Vote actual = service.get(USER_ID);
        assertMatch(actual, USER_VOTE, IGNORED_FIELDS);
        assertMatch(actual.getRestaurant(), USER_VOTE.getRestaurant(), RestaurantTestData.IGNORED_FIELDS);
    }

    @Test
    void getNotExist() {
        assertThrows(NotFoundException.class,
                () -> service.get(ADMIN_ID));
    }

    @Test
    void create() {
        Vote created = service.create(VEGANO_ID, ADMIN_ID);
        Vote actual = service.get(ADMIN_ID);
        assertMatch(created, actual, IGNORED_FIELDS);
        assertMatch(created.getRestaurant(), actual.getRestaurant(), RestaurantTestData.IGNORED_FIELDS);
    }

    @Test
    void createUserNotExist() {
        assertThrows(DataIntegrityViolationException.class,
                () -> service.create(VEGANO_ID, 1));
    }

    @Test
    void createRestaurantNotExist() {
        assertThrows(NotFoundException.class,
                () -> service.create(1, ADMIN_ID));
    }

    @Test
    void createAlreadyExist() {
        assertThrows(DataIntegrityViolationException.class,
                () -> service.create(VEGANO_ID, USER_ID));
    }

    @Test
    void update() {
        service.update(VEGANO_ID, USER_ID, LocalTime.MAX);
        assertMatch(service.get(USER_ID).getRestaurant(), VEGANO, RestaurantTestData.IGNORED_FIELDS);
    }

    @Test
    void updateTooLate() {
        assertThrows(TemporaryUnavailableOperationException.class, () ->
                service.update(VEGANO_ID, USER_ID, LocalTime.MIN)
        );
    }

    @Test
    void updateRestaurantNotExist() {
        assertThrows(DataIntegrityViolationException.class, () ->
                service.update(1, USER_ID, LocalTime.MAX));
    }

    @Test
    void updateUserNotFound() {
        assertThrows(NotFoundException.class, () ->
                service.update(VEGANO_ID, 1, LocalTime.MAX));
    }

}