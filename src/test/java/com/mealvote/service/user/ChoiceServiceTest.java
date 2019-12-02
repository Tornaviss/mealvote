package com.mealvote.service.user;

import com.mealvote.RestaurantTestData;
import com.mealvote.model.user.Choice;
import com.mealvote.util.exception.IllegalOperationException;
import com.mealvote.util.exception.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.mealvote.AssertionUtils.assertMatch;
import static com.mealvote.ChoiceTestData.IGNORED_FIELDS;
import static com.mealvote.ChoiceTestData.USER_CHOICE;
import static com.mealvote.RestaurantTestData.VEGANO;
import static com.mealvote.RestaurantTestData.VEGANO_ID;
import static com.mealvote.UserTestData.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringJUnitConfig(locations = {
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
class ChoiceServiceTest {

    @Autowired
    private ChoiceService service;

    @Test
    void get() {
        Choice actual = service.get(USER_ID);
        assertMatch(actual, USER_CHOICE, IGNORED_FIELDS);
        assertMatch(actual.getRestaurant(), USER_CHOICE.getRestaurant(), RestaurantTestData.IGNORED_FIELDS);
    }

    @Test
    void getNotExist() {
        assertThrows(NotFoundException.class,
                () -> service.get(ADMIN_ID));
    }

    @Test
    void create() {
        Choice created = service.create(VEGANO_ID, ADMIN_ID);
        Choice actual = service.get(ADMIN_ID);
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
        Choice actual = service.get(USER_ID);
        assertTrue(USER_CHOICE.getDateTime().compareTo(actual.getDateTime()) < 0);
        assertMatch(service.get(USER_ID).getRestaurant(), VEGANO, RestaurantTestData.IGNORED_FIELDS);
    }

    @Test
    void updateTooLate() {
        assertThrows(IllegalOperationException.class, () ->
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