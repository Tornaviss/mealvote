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
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(contentJson(DOMINOS, Restaurant.class, IGNORED_FIELDS));
    }

    @Test
    void getUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "/" + DOMINOS_ID))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(contentJson(
                        asSortedList(COMPARATOR, DOMINOS, MAFIA, VEGANO), Restaurant.class, IGNORED_FIELDS));
    }

    @Test
    void getAllUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + "/" + DOMINOS_ID)
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertMatch(repository.getAll(), asSortedList(COMPARATOR, MAFIA, VEGANO), IGNORED_FIELDS);
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
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + "/" + DOMINOS_ID)
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void update() throws Exception {
        Restaurant updated = getUpdated();

        mockMvc.perform(put(REST_URL + "/" + updated.getId())
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertMatch(repository.getAll(), asSortedList(COMPARATOR, updated, DOMINOS, MAFIA), IGNORED_FIELDS);
    }

    @Test
    void updateNotFound() throws Exception {
        Restaurant updated = getUpdated();
        updated.setId(1);

        mockMvc.perform(put(REST_URL + "/" + updated.getId())
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void updateNameInvalid() throws Exception {
        Restaurant updated = getUpdated();
        updated.setName("a");

        mockMvc.perform(put(REST_URL + "/" + updated.getId())
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateNameDuplicate() throws Exception {
        Restaurant updated = getUpdated();
        updated.setName(DOMINOS.getName());

        mockMvc.perform(put(REST_URL + "/" + updated.getId())
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void updateForbidden() throws Exception {
        Restaurant updated = getUpdated();

        mockMvc.perform(put(REST_URL + "/" + updated.getId())
                .with(userHttpBasic(USER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void create() throws Exception {
        Restaurant created = getCreated();

        ResultActions action = mockMvc.perform(post(REST_URL)
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(created)))
                .andDo(print())
                .andExpect(status().isCreated());

        Restaurant returned = readFromJson(action, Restaurant.class);
        created.setId(returned.getId());
        assertMatch(returned, created, IGNORED_FIELDS);
        assertMatch(repository.getAll(),
                asSortedList(COMPARATOR, created, DOMINOS, VEGANO, MAFIA), IGNORED_FIELDS);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createNameDuplicate() throws Exception {
        Restaurant created = getCreated();
        created.setName(DOMINOS.getName());

        mockMvc.perform(post(REST_URL)
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(created)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void createNameInvalid() throws Exception {
        Restaurant created = getCreated();
        created.setName("a");

        mockMvc.perform(post(REST_URL)
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(created)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createForbidden() throws Exception {
        Restaurant created = getCreated();
        mockMvc.perform(post(REST_URL)
                .with(userHttpBasic(USER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(created)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void getWithChoices() throws Exception {
        ResultActions action = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "/" + DOMINOS_ID)
                .param("includeChoices", "true")
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(contentJson(DOMINOS, Restaurant.class, IGNORED_FIELDS));

        Restaurant returned = readFromJson(action, Restaurant.class);
        assertMatch(returned.getChoices(), Collections.singletonList(USER_CHOICE), ChoiceTestData.IGNORED_FIELDS);
    }

    @Test
    void getWithChoicesNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "/" + 1)
                .param("includeChoices", "true")
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getWithChoicesUnauthorised() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "/" + 1)
                .param("includeChoices", "true"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
