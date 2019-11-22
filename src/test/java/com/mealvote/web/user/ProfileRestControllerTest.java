package com.mealvote.web.user;

import com.mealvote.model.user.User;
import com.mealvote.service.user.UserService;
import com.mealvote.web.AbstractRestControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static com.mealvote.AssertionUtils.assertMatch;
import static com.mealvote.AssertionUtils.contentJson;
import static com.mealvote.JsonParseUtils.readFromJson;
import static com.mealvote.UserTestData.*;
import static com.mealvote.web.user.ProfileRestController.REST_URL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProfileRestControllerTest extends AbstractRestControllerTest {

    @Autowired
    private UserService service;

    @Test
    void get() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(USER)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(USER, User.class, IGNORED_FIELDS));
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL)
                .with(userHttpBasic(USER)))
                .andExpect(status().isNoContent());
        assertMatch(service.getAll(), Collections.singletonList(ADMIN), IGNORED_FIELDS);
    }

    @Test
    void register() throws Exception {
        User created = getCreated();

        ResultActions action = mockMvc.perform(post(REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(created, created.getPassword())))
                .andDo(print())
                .andExpect(status().isCreated());
        User returned = readFromJson(action, User.class);

        created.setId(returned.getId());
        assertMatch(returned, created, IGNORED_FIELDS);
        assertMatch(service.getByEmail(created.getEmail()), created, IGNORED_FIELDS);
    }

    @Test
    void update() throws Exception {
        User updated = getUpdated();

        mockMvc.perform(put(REST_URL)
                .with(userHttpBasic(USER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertMatch(service.getByEmail(updated.getEmail()), updated, IGNORED_FIELDS);
    }

}