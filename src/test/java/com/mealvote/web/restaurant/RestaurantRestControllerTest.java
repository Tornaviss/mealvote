package com.mealvote.web.restaurant;

import com.mealvote.ChoiceTestData;
import com.mealvote.model.restaurant.Restaurant;
import com.mealvote.repository.restaurant.CrudRestaurantRepository;
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
import static com.mealvote.ChoiceTestData.USER_CHOICE;
import static com.mealvote.JsonParseUtils.readFromJson;
import static com.mealvote.RestaurantTestData.*;
import static com.mealvote.UserTestData.ADMIN;
import static com.mealvote.UserTestData.USER;
import static com.mealvote.UserTestData.userHttpBasic;
import static com.mealvote.web.restaurant.RestaurantRestController.REST_URL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RestaurantRestControllerTest extends AbstractRestControllerTest {

    @Autowired
    private CrudRestaurantRepository repository;

    @Test
    void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "/" + DOMINOS_ID)
                .with(userHttpBasic(USER)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(contentJson(DOMINOS, Restaurant.class, IGNORED_FIELDS));
    }

    @Test
    void getNotAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "/" + DOMINOS_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(USER)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(contentJson(
                        asSortedList(COMPARATOR, DOMINOS, MAFIA, VEGANO), Restaurant.class, IGNORED_FIELDS));
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + "/" + DOMINOS_ID)
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isNoContent())
                .andDo(print());

        assertMatch(repository.getAll(), asSortedList(COMPARATOR, MAFIA, VEGANO), IGNORED_FIELDS);
    }

    @Test
    void update() throws Exception {
        Restaurant updated = getUpdated();

        mockMvc.perform(put(REST_URL + "/" + VEGANO_ID)
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isNoContent())
                .andDo(print());

        assertMatch(repository.getAll(), asSortedList(COMPARATOR, updated, DOMINOS, MAFIA), IGNORED_FIELDS);
    }

    @Test
    void create() throws Exception {
        Restaurant created = getCreated();

        ResultActions action = mockMvc.perform(post(REST_URL)
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(created)))
                .andExpect(status().isCreated())
                .andDo(print());

        Restaurant returned = readFromJson(action, Restaurant.class);
        created.setId(returned.getId());
        assertMatch(returned, created, IGNORED_FIELDS);
        assertMatch(repository.getAll(),
                asSortedList(COMPARATOR, created, DOMINOS, VEGANO, MAFIA), IGNORED_FIELDS);
    }

    @Test
    void createNotAuth() throws Exception {
        Restaurant created = getCreated();
        mockMvc.perform(post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(created)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createForbidden() throws Exception {
        Restaurant created = getCreated();
        mockMvc.perform(post(REST_URL)
                .with(userHttpBasic(USER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(created)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void getWithChoices() throws Exception {
        ResultActions action = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "/" + DOMINOS_ID)
                .param("includeChoices", "true")
                .with(userHttpBasic(USER)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(contentJson(DOMINOS, Restaurant.class, IGNORED_FIELDS));

        Restaurant returned = readFromJson(action, Restaurant.class);

        assertMatch(returned.getChoices(), Collections.singletonList(USER_CHOICE), ChoiceTestData.IGNORED_FIELDS);
    }
}
