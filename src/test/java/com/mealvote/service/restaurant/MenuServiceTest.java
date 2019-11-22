package com.mealvote.service.restaurant;

import com.mealvote.MenuTestData;
import com.mealvote.model.restaurant.Menu;
import com.mealvote.util.exception.IllegalOperationException;
import com.mealvote.util.exception.IllegalRequestDataException;
import com.mealvote.util.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.Collections;

import static com.mealvote.AssertionUtils.asSortedList;
import static com.mealvote.AssertionUtils.assertMatch;
import static com.mealvote.MenuTestData.*;
import static com.mealvote.RestaurantTestData.DOMINOS_ID;
import static com.mealvote.RestaurantTestData.VEGANO_ID;
import static com.mealvote.RestaurantTestData.MAFIA_ID;
import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(locations = {
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
class MenuServiceTest {

    @Autowired
    private MenuService service;

    @Autowired
    private DishService dishService;

    @Test
    void get() {
        assertMatch(service.get(DOMINOS_ID), DOMINOS_MENU, MENU_IGNORED_FIELDS);
    }

    @Test
    void getNotFound() {
        assertThrows(NotFoundException.class, () ->
                service.get(1)
        );
    }

    @Test
    void create() {
        Menu newMenu = MenuTestData.getCreated();
        Menu created = service.create(newMenu, MAFIA_ID);
        newMenu.setId(created.getId());
        assertMatch(created, newMenu, MENU_IGNORED_FIELDS);
        assertMatch(created.getDishes(), newMenu.getDishes(), DISH_IGNORED_FIELDS);
    }

    @Test
    void createNull() {
        assertThrows(IllegalArgumentException.class, () ->
                service.create(null, MAFIA_ID)
        );
    }

    @Test
    void createRestaurantNotExist() {
        assertThrows(NotFoundException.class, () ->
                service.create(MenuTestData.getCreated(), 1)
        );
    }

    @Test
    void createAlreadyExist() {
        assertThrows(IllegalOperationException.class, () ->
                service.create(getCreated(), DOMINOS_ID)
        );
    }

    @Test
    void createNotNew() {
        assertThrows(IllegalRequestDataException.class, () ->
                service.create(getUpdated(), VEGANO_ID)
        );
    }

    @Test
    void update() {
        Menu updated = getUpdated();
        service.update(updated, VEGANO_ID);
        Menu actual = service.get(VEGANO_ID);
        assertMatch(actual, updated, MENU_IGNORED_FIELDS);

        String[] dishIgnoredFieldsWithId = Arrays.copyOf(DISH_IGNORED_FIELDS, DISH_IGNORED_FIELDS.length + 1);
        dishIgnoredFieldsWithId[DISH_IGNORED_FIELDS.length] = "id";
        assertMatch(dishService.getAllForMenu(VEGANO_ID), updated.getDishes(), dishIgnoredFieldsWithId);
    }

    @Test
    void updateNull() {
        assertThrows(IllegalArgumentException.class, () ->
                service.update(null, MAFIA_ID)
        );
    }

    @Test
    void updateNew() {
        assertThrows(IllegalRequestDataException.class, () ->
                service.update(getUpdated(), MAFIA_ID)
        );
    }

    @Test
    void updateNotExist() {
        Menu updated = getUpdated();
        updated.setId(MAFIA_ID);
        assertThrows(IllegalOperationException.class, () ->
                service.update(updated, MAFIA_ID)
        );
    }

    @Test
    void updateRestaurantNotExist() {
        assertThrows(IllegalRequestDataException.class, () ->
                service.update(getUpdated(), 1)
        );
    }

    @Test
    void delete() {
        service.delete(DOMINOS_ID);
        assertMatch(dishService.getAllForMenu(DOMINOS_ID), Collections.EMPTY_LIST, DISH_IGNORED_FIELDS);
        assertThrows(NotFoundException.class, () ->
                service.get(DOMINOS_ID)
        );
    }

    @Test
    void deleteNotFound() {
        assertThrows(NotFoundException.class, () ->
                service.delete(1)
        );
    }

    @Test
    void getAll() {
        assertMatch(service.getAll(),
                asSortedList(MENU_COMPARATOR, DOMINOS_MENU, VEGANO_MENU),
                MENU_IGNORED_FIELDS);
    }
}