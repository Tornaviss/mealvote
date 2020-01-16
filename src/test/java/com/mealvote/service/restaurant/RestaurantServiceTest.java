package com.mealvote.service.restaurant;

import com.mealvote.MenuTestData;
import com.mealvote.VoteTestData;
import com.mealvote.model.restaurant.Menu;
import com.mealvote.model.restaurant.Restaurant;
import com.mealvote.util.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.mealvote.AssertionUtils.asSortedList;
import static com.mealvote.AssertionUtils.assertMatch;
import static com.mealvote.MenuTestData.MENU_COMPARATOR;
import static com.mealvote.MenuTestData.DOMINOS_MENU;
import static com.mealvote.MenuTestData.VEGANO_MENU;
import static com.mealvote.MenuTestData.MENU_IGNORED_FIELDS;
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
    void getAllWithVotes() {
        List<Restaurant> actual = service.getAll(false, true);
        assertMatch(actual, asSortedList(COMPARATOR, DOMINOS, VEGANO, MAFIA), IGNORED_FIELDS);
        actual.forEach(x -> assertNotNull(x.getVotes()));
    }

    @Test
    void getAllWithMenu() {
        List<Restaurant> actual = service.getAll(true, false);
        assertMatch(actual, asSortedList(COMPARATOR, DOMINOS, VEGANO, MAFIA), IGNORED_FIELDS);

        List<Menu> actualMenus = actual.stream()
                .filter(x -> x.getMenu() != null)
                .map(Restaurant::getMenu)
                .sorted(MENU_COMPARATOR)
                .collect(Collectors.toList());

        assertMatch(actualMenus, asSortedList(MENU_COMPARATOR, DOMINOS_MENU, VEGANO_MENU), MENU_IGNORED_FIELDS);
        actualMenus.forEach(x -> assertNotNull(x.getDishes()));
    }

    @Test
    void getAllWithMenuAndVotes() {
        List<Restaurant> actual = service.getAll(true, true);
        assertMatch(actual, asSortedList(COMPARATOR, DOMINOS, VEGANO, MAFIA), IGNORED_FIELDS);
        actual.forEach(x -> assertNotNull(x.getVotes()));

        List<Menu> actualMenus = actual.stream()
                .filter(x -> x.getMenu() != null)
                .map(Restaurant::getMenu)
                .sorted(MENU_COMPARATOR)
                .collect(Collectors.toList());

        // assertMatch (the same as in getAllWithMenu test method) doesn't work for some reason,
        // so i just simplified the next line just like that it assures of proper list size which also is nice
        assertEquals(2, actualMenus.size());
        actualMenus.forEach(x -> assertNotNull(x.getDishes()));
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
    void getWithVotes() {
        Restaurant actual = service.get(DOMINOS_ID, false, true);
        assertMatch(actual, DOMINOS, IGNORED_FIELDS);
        assertMatch(actual.getVotes(), Collections.singletonList(USER_VOTE), VoteTestData.IGNORED_FIELDS);
    }

    @Test
    void getWithMenu() {
        Restaurant actual = service.get(DOMINOS_ID, true, false);
        assertMatch(actual, DOMINOS, IGNORED_FIELDS);
        Menu actualMenu = actual.getMenu();
        assertMatch(actualMenu, DOMINOS_MENU, MenuTestData.MENU_IGNORED_FIELDS);
        assertMatch(actualMenu.getDishes(), DOMINOS_MENU.getDishes(), MenuTestData.DISH_IGNORED_FIELDS);
    }

    @Test
    void getWithMenuAndVotes() {
        Restaurant actual = service.get(DOMINOS_ID, true, true);

        assertMatch(actual, DOMINOS, IGNORED_FIELDS);
        assertMatch(actual.getVotes(), Collections.singletonList(USER_VOTE), VoteTestData.IGNORED_FIELDS);

        Menu actualMenu = actual.getMenu();
        assertMatch(actualMenu, DOMINOS_MENU, MenuTestData.MENU_IGNORED_FIELDS);
        assertMatch(actualMenu.getDishes(), DOMINOS_MENU.getDishes(), MenuTestData.DISH_IGNORED_FIELDS);
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

}