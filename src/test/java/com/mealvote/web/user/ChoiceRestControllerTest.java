package com.mealvote.web.user;

import com.mealvote.RestaurantTestData;
import com.mealvote.model.user.Choice;
import com.mealvote.service.user.ChoiceService;
import com.mealvote.web.AbstractRestControllerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalTime;

import static com.mealvote.AssertionUtils.assertMatch;
import static com.mealvote.AssertionUtils.contentJson;
import static com.mealvote.ChoiceTestData.IGNORED_FIELDS;
import static com.mealvote.ChoiceTestData.USER_CHOICE;
import static com.mealvote.JsonParseUtils.readFromJson;
import static com.mealvote.RestaurantTestData.*;
import static com.mealvote.UserTestData.*;
import static com.mealvote.web.user.ChoiceRestController.REST_URL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChoiceRestControllerTest extends AbstractRestControllerTest {

    @Autowired
    private ChoiceService service;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(USER)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(USER_CHOICE, Choice.class, IGNORED_FIELDS));
    }

    @Test
    @Transactional
    void update() throws Exception {
        ResultActions action = mockMvc.perform(
                put(REST_URL + "/" + VEGANO_ID)
                .with(userHttpBasic(USER)));

        if (LocalTime.now().compareTo(LocalTime.of(11, 0)) < 0) {
           action.andExpect(status().isNoContent());
            Choice updated = service.get(USER_ID);
            Assertions.assertEquals(VEGANO_ID, updated.getRestaurant().getId());
            Assertions.assertEquals(VEGANO.getName(), updated.getRestaurant().getName());
        } else {
            action.andExpect(status().isConflict());
        }
    }

    @Test
    void create() throws Exception {
        ResultActions action = mockMvc.perform(post(REST_URL + "/" + DOMINOS_ID)
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isCreated())
                .andDo(print());

        Choice returned = readFromJson(action, Choice.class);
        Choice actual = service.get(ADMIN_ID);
        System.out.println(returned);
        System.out.println(actual);
        assertMatch(actual, returned, IGNORED_FIELDS);
        assertMatch(actual.getRestaurant(), returned.getRestaurant(), RestaurantTestData.IGNORED_FIELDS);
    }

}