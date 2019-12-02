package com.mealvote.service.restaurant;

import com.mealvote.model.restaurant.Dish;
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
import static com.mealvote.MenuTestData.*;
import static com.mealvote.RestaurantTestData.DOMINOS_ID;
import static com.mealvote.RestaurantTestData.VEGANO_ID;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringJUnitConfig(locations = {
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
class DishServiceTest {

    @Autowired
    private DishService service;

    @Test
    void get() {
        assertMatch(service.get(VEGANO_DISH1_ID), VEGANO_DISH1, DISH_IGNORED_FIELDS);
    }

    @Test
    void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(1));
    }

    @Test
    void getAll() {
        assertMatch(service.getAll(),
                asSortedList(DISH_COMPARATOR, DOMINOS_DISH1, DOMINOS_DISH2, VEGANO_DISH1, VEGANO_DISH2),
                DISH_IGNORED_FIELDS
        );
    }

    @Test
    void getAllForMenu() {
        assertMatch(service.getAllForMenu(DOMINOS_ID),
                asSortedList(DISH_COMPARATOR, DOMINOS_DISH1, DOMINOS_DISH2),
                DISH_IGNORED_FIELDS);
    }

    @Test
    void getAllForMenuNotExist() {
        assertMatch(service.getAllForMenu(1), Collections.emptyList(), DISH_IGNORED_FIELDS);
    }

    @Test
    void delete() {
        service.delete(DOMINOS_DISH1_ID);
        assertMatch(service.getAllForMenu(DOMINOS_ID), asSortedList(DISH_COMPARATOR, DOMINOS_DISH2), DISH_IGNORED_FIELDS);
    }

    @Test
    void deleteNotFound() {
        assertThrows(NotFoundException.class,
                () -> service.delete(1));
    }

    @Test
    void create() {
        Dish newDish = getCreatedDish();
        Dish created = service.create(newDish, DOMINOS_ID);
        newDish.setId(created.getId());
        assertMatch(created, newDish, DISH_IGNORED_FIELDS);
        assertMatch(service.getAllForMenu(DOMINOS_ID),
                asSortedList(DISH_COMPARATOR, created, DOMINOS_DISH2, DOMINOS_DISH1), DISH_IGNORED_FIELDS);
    }

    @Test
    void createMenuNotExist() {
        Dish newDish = getCreatedDish();
        assertThrows(DataIntegrityViolationException.class,
                () -> service.create(newDish, 1));
    }

    @Test
    void createNull() {
        assertThrows(IllegalArgumentException.class,
                () -> service.create(null, 1));
    }

    @Test
    void createNameDuplicate() {
        Dish newDish = getCreatedDish();
        newDish.setName(DOMINOS_DISH1.getName());
        assertThrows(DataIntegrityViolationException.class,
                () -> service.create(newDish, DOMINOS_ID));
    }

    @Test
    void update() {
        Dish updated = getUpdatedDish();
        service.update(updated);
        assertMatch(service.get(updated.getId()), updated, DISH_IGNORED_FIELDS);
        assertMatch(service.getAllForMenu(VEGANO_ID),
                asSortedList(DISH_COMPARATOR, VEGANO_DISH2, updated), DISH_IGNORED_FIELDS);
    }

    @Test
    void updateNotFound() {
        Dish updated = getUpdatedDish();
        updated.setId(1);
        assertThrows(NotFoundException.class, () -> service.update(updated));
    }

    @Test
    void updateNull() {
        assertThrows(IllegalArgumentException.class, () -> service.update(null));
    }
    @Test
    void updateNameDuplicate() {
        Dish updated = getUpdatedDish();
        updated.setName(VEGANO_DISH2.getName());
        assertThrows(DataIntegrityViolationException.class,
                () -> service.update(updated));
    }
}