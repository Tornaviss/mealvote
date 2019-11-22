package com.mealvote.service.user;

import com.mealvote.RestaurantTestData;
import com.mealvote.model.user.Choice;
import com.mealvote.util.exception.IllegalOperationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
    void create() {
        Choice created = service.create(VEGANO_ID, ADMIN_ID);
        Choice actual = service.get(ADMIN_ID);
        assertMatch(created, actual, IGNORED_FIELDS);
        assertMatch(created.getRestaurant(), actual.getRestaurant(), RestaurantTestData.IGNORED_FIELDS);
    }

    @Test
    void update() {
        if (LocalTime.now().compareTo(LocalTime.of(11, 0)) < 0) {
            service.update(VEGANO_ID, USER_ID);
            Choice actual = service.get(USER_ID);
            assertTrue(USER_CHOICE.getDateTime().compareTo(actual.getDateTime()) < 0);
            assertMatch(service.get(USER_ID).getRestaurant(), VEGANO, RestaurantTestData.IGNORED_FIELDS);
        } else {
            assertThrows(IllegalOperationException.class, () ->
                    service.update(VEGANO_ID, USER_ID)
            );
        }
    }
}