package com.mealvote.web.restaurant;

import com.mealvote.model.restaurant.Dish;
import com.mealvote.model.restaurant.Menu;
import com.mealvote.service.restaurant.DishService;
import com.mealvote.service.restaurant.MenuService;
import com.mealvote.web.AbstractRestControllerTest;
import com.mealvote.web.json.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.mealvote.AssertionUtils.*;
import static com.mealvote.JsonParseUtils.readFromJson;
import static com.mealvote.MenuTestData.*;
import static com.mealvote.RestaurantTestData.MAFIA_ID;
import static com.mealvote.RestaurantTestData.VEGANO_ID;
import static com.mealvote.RestaurantTestData.DOMINOS_ID;
import static com.mealvote.RestaurantTestData.DOMINOS;
import static com.mealvote.UserTestData.ADMIN;
import static com.mealvote.UserTestData.USER;
import static com.mealvote.UserTestData.userHttpBasic;
import static com.mealvote.web.restaurant.MenuRestController.REST_URL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MenuRestControllerTest extends AbstractRestControllerTest {

    @Autowired
    private MenuService service;

    @Autowired
    private DishService dishService;

    @Test
    void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "/" + VEGANO_ID)
        .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(contentJson(VEGANO_MENU, Menu.class, MENU_IGNORED_FIELDS));
    }

    @Test
    void getNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "/" + 1)
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "/" + 1))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + "/" + VEGANO_ID)
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertMatch(service.getAll(), Collections.singletonList(DOMINOS_MENU), MENU_IGNORED_FIELDS);
    }

    @Test
    void deleteNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + "/" + 1)
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + "/" + 1)
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void update() throws Exception {
        Menu updated = getUpdated();

        mockMvc.perform(put(REST_URL + "/" + updated.getId())
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertMatch(service.get(VEGANO_ID), updated, MENU_IGNORED_FIELDS);
        String[] dishIgnoredFieldsWithId = Arrays.copyOf(DISH_IGNORED_FIELDS, DISH_IGNORED_FIELDS.length + 1);
        dishIgnoredFieldsWithId[DISH_IGNORED_FIELDS.length] = "id";
        assertMatch(dishService.getAllForMenu(VEGANO_ID), updated.getDishes(), dishIgnoredFieldsWithId);
    }

    @Test
    void updateNotFound() throws Exception {
        Menu updated = getUpdated();
        updated.setId(1);

        mockMvc.perform(put(REST_URL + "/" + updated.getId())
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateDishNameDuplicate() throws Exception {
        Menu updated = getUpdated();
        updated.setDishes(
                List.of(VEGANO_DISH1, new Dish(VEGANO_DISH1.getName(), 500))
        );

        mockMvc.perform(put(REST_URL + "/" + updated.getId())
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void updateForbidden() throws Exception {
        Menu updated = getUpdated();

        mockMvc.perform(put(REST_URL + "/" + updated.getId())
                .with(userHttpBasic(USER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void updateDishesCountNotValid() throws Exception {
        Menu updated = getUpdated();
        updated.setDishes(List.of(VEGANO_DISH1));

        mockMvc.perform(put(REST_URL + "/" + updated.getId())
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateDishesNameNotValid() throws Exception {
        Menu updated = getUpdated();
        updated.getDishes().get(0).setName("a");

        mockMvc.perform(put(REST_URL + "/" + updated.getId())
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateDishesPriceNotValid() throws Exception {
        Menu updated = getUpdated();
        updated.getDishes().get(0).setPrice(0);

        mockMvc.perform(put(REST_URL + "/" + updated.getId())
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void create() throws Exception {
        Menu created = getCreated();

        ResultActions action = mockMvc.perform(post(String.format("/restaurants/%d/menu", MAFIA_ID))
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(created)))
                .andDo(print())
                .andExpect(status().isCreated());

        Menu returned = readFromJson(action, Menu.class);
        created.setId(returned.getId());
        assertMatch(returned, created, MENU_IGNORED_FIELDS);
        assertMatch(service.getAll(),
                asSortedList(MENU_COMPARATOR, returned, DOMINOS_MENU, VEGANO_MENU), MENU_IGNORED_FIELDS);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createAlreadyExist() throws Exception {
        Menu created = getCreated();

        mockMvc.perform(post(String.format("/restaurants/%d/menu", DOMINOS_ID))
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(created)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createRestaurantNotExist() throws Exception {
        Menu created = getCreated();

        mockMvc.perform(post(String.format("/restaurants/%d/menu", 1))
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(created)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void createForbidden() throws Exception {
        Menu created = getCreated();

        mockMvc.perform(post(String.format("/restaurants/%d/menu", MAFIA_ID))
                .with(userHttpBasic(USER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(created)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void createDishesCountNotValid() throws Exception {
        Menu created = getCreated();
        created.setDishes(List.of(DOMINOS_DISH1));

        mockMvc.perform(post(String.format("/restaurants/%d/menu", MAFIA_ID))
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(created)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createDishesNameNotValid() throws Exception {
        Menu created = getCreated();
        created.getDishes().get(0).setName("a");

        mockMvc.perform(post(String.format("/restaurants/%d/menu", MAFIA_ID))
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(created)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createDishesPriceNotValid() throws Exception {
        Menu created = getCreated();
        created.getDishes().get(0).setPrice(0);

        mockMvc.perform(post(String.format("/restaurants/%d/menu", MAFIA_ID))
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(created)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }
}