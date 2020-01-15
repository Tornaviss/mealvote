package com.mealvote.web.user;

import com.mealvote.RestaurantTestData;
import com.mealvote.model.user.Vote;
import com.mealvote.service.user.VoteService;
import com.mealvote.util.DateTimeUtil;
import com.mealvote.web.AbstractRestControllerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.mealvote.AssertionUtils.assertMatch;
import static com.mealvote.AssertionUtils.contentJson;
import static com.mealvote.VoteTestData.IGNORED_FIELDS;
import static com.mealvote.VoteTestData.USER_VOTE;
import static com.mealvote.JsonParseUtils.readFromJson;
import static com.mealvote.RestaurantTestData.*;
import static com.mealvote.UserTestData.*;
import static com.mealvote.web.user.VoteRestController.REST_URL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VoteRestControllerTest extends AbstractRestControllerTest {

    @Autowired
    private VoteService service;

    private static final String MAX_TIME_FORMATTED;
    private static final String MIN_TIME_FORMATTED;

    static {
        MAX_TIME_FORMATTED = LocalTime.MAX.format(DateTimeFormatter.ofPattern(DateTimeUtil.TIME_PATTERN));
        MIN_TIME_FORMATTED = LocalTime.MIN.format(DateTimeFormatter.ofPattern(DateTimeUtil.TIME_PATTERN));
    }

    @Test
    void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(USER_VOTE, Vote.class, IGNORED_FIELDS));
    }

    @Test
    void getNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void update() throws Exception {
        mockMvc.perform(
                put(REST_URL + "/" + MAX_TIME_FORMATTED)
                        .param("restaurantId", VEGANO_ID.toString())
                        .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isNoContent());
        Vote updated = service.get(USER_ID);
        Assertions.assertEquals(VEGANO_ID, updated.getRestaurant().getId());
        Assertions.assertEquals(VEGANO.getName(), updated.getRestaurant().getName());
    }

    @Test
    void updateNotFound() throws Exception {
        mockMvc.perform(
                put(REST_URL + "/"
                        + MAX_TIME_FORMATTED)
                        .param("restaurantId", VEGANO_ID.toString())
                        .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateRestaurantNotFound() throws Exception {
        mockMvc.perform(
                put(REST_URL + "/" + MAX_TIME_FORMATTED)
                        .param("restaurantId", "1")
                        .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTooLate() throws Exception {
        mockMvc.perform(
                put(REST_URL + "/" + MIN_TIME_FORMATTED)
                        .param("restaurantId", VEGANO_ID.toString())
                        .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void updateUnauthorized() throws Exception {
        mockMvc.perform(
                put(REST_URL + "/" + MAX_TIME_FORMATTED)
                        .param("restaurantId", "1"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void create() throws Exception {
        ResultActions action = mockMvc.perform(post(REST_URL)
                .param("restaurantId", DOMINOS_ID.toString())
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isCreated());

        Vote returned = readFromJson(action, Vote.class);
        Vote actual = service.get(ADMIN_ID);
        assertMatch(actual, returned, IGNORED_FIELDS);
        assertMatch(actual.getRestaurant(), returned.getRestaurant(), RestaurantTestData.IGNORED_FIELDS);
    }

    @Test
    void createRestaurantNotFound() throws Exception {
        mockMvc.perform(post(REST_URL)
                .param("restaurantId", "1")
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createAlreadyExist() throws Exception {
        mockMvc.perform(post(REST_URL)
                .param("restaurantId", VEGANO_ID.toString())
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void createUnauthorized() throws Exception {
        mockMvc.perform(post(REST_URL)
                .param("restaurantId", VEGANO_ID.toString()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

}