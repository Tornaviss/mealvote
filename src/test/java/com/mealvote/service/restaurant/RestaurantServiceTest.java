package com.mealvote.service.restaurant;

import com.mealvote.model.restaurant.Restaurant;
import com.mealvote.util.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static com.mealvote.AssertionUtils.asSortedList;
import static com.mealvote.AssertionUtils.assertMatch;
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
    void create() {
        Restaurant newRestaurant = getCreated();
        Restaurant created = service.create(newRestaurant);
        newRestaurant.setId(created.getId());
        assertMatch(created, newRestaurant, IGNORED_FIELDS);
        assertMatch(service.getAll(),
                asSortedList(COMPARATOR, newRestaurant, DOMINOS, VEGANO, MAFIA), IGNORED_FIELDS);
    }

    @Test
    void delete() {
        service.delete(DOMINOS_ID);
        assertMatch(service.getAll(), asSortedList(COMPARATOR, VEGANO, MAFIA), IGNORED_FIELDS);
        assertThrows(NotFoundException.class, () -> service.get(DOMINOS_ID));
    }

    @Test
    void update() {
        Restaurant updated = getUpdated();
        service.update(updated, VEGANO_ID);
        assertMatch(service.getAll(), asSortedList(COMPARATOR, updated, DOMINOS, MAFIA), IGNORED_FIELDS);
    }

    @Test
    void updateNotFound(){
        Restaurant updated = getUpdated();
        updated.setId(1);
        assertThrows(NotFoundException.class, () -> service.update(updated, updated.getId()));
    }

}