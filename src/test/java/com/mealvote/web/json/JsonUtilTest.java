package com.mealvote.web.json;

import com.mealvote.UserTestData;
import com.mealvote.model.restaurant.Dish;
import com.mealvote.model.user.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.mealvote.AssertionUtils.assertMatch;
import static com.mealvote.MenuTestData.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonUtilTest {

    @Test
    void readWriteValue() throws Exception {
        String json = JsonUtil.writeValue(DOMINOS_DISH1);
        System.out.println(json);
        Dish dish = JsonUtil.readValue(json, Dish.class);
        assertMatch(dish, DOMINOS_DISH1, DISH_IGNORED_FIELDS);
    }

    @Test
    void readWriteValues() throws Exception {
        String json = JsonUtil.writeValue(DOMINOS_MENU.getDishes());
        System.out.println(json);
        List<Dish> dishes = JsonUtil.readValues(json, Dish.class);
        assertMatch(dishes, DOMINOS_MENU.getDishes(), DISH_IGNORED_FIELDS);
    }

    @Test
    void writeOnlyAccess() throws Exception {
        String json = JsonUtil.writeValue(UserTestData.USER);
        System.out.println(json);
        assertThat(json, not(containsString("password")));
        String jsonWithPass = UserTestData.jsonWithPassword(UserTestData.USER, "newPass");
        System.out.println(jsonWithPass);
        User user = JsonUtil.readValue(jsonWithPass, User.class);
        assertEquals(user.getPassword(), "newPass");
    }
}