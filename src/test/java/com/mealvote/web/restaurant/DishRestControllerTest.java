package com.mealvote.web.restaurant;

import com.mealvote.model.restaurant.Dish;
import com.mealvote.service.restaurant.DishService;
import com.mealvote.web.AbstractRestControllerTest;
import com.mealvote.web.json.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static com.mealvote.AssertionUtils.*;
import static com.mealvote.JsonParseUtils.readFromJson;
import static com.mealvote.MenuTestData.*;
import static com.mealvote.RestaurantTestData.DOMINOS_ID;
import static com.mealvote.RestaurantTestData.VEGANO_ID;
import static com.mealvote.UserTestData.ADMIN;
import static com.mealvote.UserTestData.USER;
import static com.mealvote.UserTestData.userHttpBasic;
import static com.mealvote.web.restaurant.DishRestController.REST_URL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DishRestControllerTest extends AbstractRestControllerTest {

    @Autowired
    private DishService service;

    @Test
    void getAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(contentJson(service.getAll(), Dish.class, DISH_IGNORED_FIELDS));
    }

    @Test
    void getAllUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void get() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get(REST_URL + "/" + DOMINOS_DISH1_ID)
                        .with(userHttpBasic(USER)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(contentJson(DOMINOS_DISH1, Dish.class, DISH_IGNORED_FIELDS));
    }

    @Test
    void getNotFound() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get(REST_URL + "/" + 1)
                        .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getUnauthorized() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get(REST_URL + "/" + VEGANO_DISH1_ID))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .delete(REST_URL + "/" + DOMINOS_DISH1_ID)
                        .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertMatch(service.getAllForMenu(DOMINOS_ID),
                Collections.singletonList(DOMINOS_DISH2), DISH_IGNORED_FIELDS);
    }

    @Test
    void deleteNotFound() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .delete(REST_URL + "/" + 1)
                        .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteForbidden() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .delete(REST_URL + "/" + VEGANO_DISH1_ID)
                        .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isForbidden());

    }

    @Test
    void update() throws Exception {
        Dish updated = getUpdatedDish();

        mockMvc.perform(put(REST_URL + "/" + updated.getId())
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertMatch(service.getAllForMenu(VEGANO_ID),
                asSortedList(DISH_COMPARATOR, updated, VEGANO_DISH2), DISH_IGNORED_FIELDS);
    }

    @Test
    void updateDifferentIdInEntity() throws Exception {
        Dish updated = getUpdatedDish();
        updated.setId(1);

        mockMvc.perform(put(REST_URL + "/" + VEGANO_DISH1_ID)
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateNotFound() throws Exception {
        Dish updated = getUpdatedDish();
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
    void updateNameDuplicate() throws Exception {
        Dish updated = getUpdatedDish();
        updated.setName(VEGANO_DISH2.getName());

        mockMvc.perform(put(REST_URL + "/" + updated.getId())
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void updateForbidden() throws Exception {
        Dish updated = getUpdatedDish();

        mockMvc.perform(put(REST_URL + "/" + updated.getId())
                .with(userHttpBasic(USER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void updatePriceNotValid() throws Exception {
        Dish updated = getUpdatedDish();
        updated.setPrice(0);

        mockMvc.perform(put(REST_URL + "/" + updated.getId())
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateNameNotValid() throws Exception {
        Dish updated = getUpdatedDish();
        updated.setName("a");

        mockMvc.perform(put(REST_URL + "/" + updated.getId())
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void create() throws Exception {
        Dish created = getCreatedDish();

        ResultActions action = mockMvc.perform(post("/menus/" + VEGANO_ID + "/dishes")
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(created)))
                .andDo(print())
                .andExpect(status().isCreated());

        Dish returned = readFromJson(action, Dish.class);
        created.setId(returned.getId());
        assertMatch(returned, created, DISH_IGNORED_FIELDS);
        assertMatch(service.getAllForMenu(VEGANO_ID),
                asSortedList(DISH_COMPARATOR, created, VEGANO_DISH1, VEGANO_DISH2), DISH_IGNORED_FIELDS);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createNameDuplicate() throws Exception {
        Dish created = getCreatedDish();
        created.setName(VEGANO_DISH2.getName());

        mockMvc.perform(post("/menus/" + VEGANO_ID + "/dishes")
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(created)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void createForbidden() throws Exception {
        Dish created = getCreatedDish();

        mockMvc.perform(post("/menus/" + VEGANO_ID + "/dishes")
                .with(userHttpBasic(USER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(created)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void createNameNotValid() throws Exception {
        Dish created = getCreatedDish();
        created.setName("a");

        mockMvc.perform(post("/menus/" + VEGANO_ID + "/dishes")
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(created)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createPriceNotValid() throws Exception {
        Dish created = getCreatedDish();
        created.setPrice(0);

        mockMvc.perform(post("/menus/" + VEGANO_ID + "/dishes")
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(created)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

}