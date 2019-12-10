package com.mealvote.web.user;

import com.mealvote.model.user.User;
import com.mealvote.service.user.UserService;
import com.mealvote.web.AbstractRestControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static com.mealvote.UserTestData.InvalidUserProperty.*;
import static com.mealvote.AssertionUtils.*;
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
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(USER, User.class, IGNORED_FIELDS));
    }

    @Test
    void getUnauthorized() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL)
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertMatch(service.getAll(), Collections.singletonList(ADMIN), IGNORED_FIELDS);
    }

    @Test
    void deleteUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete(REST_URL))
                .andDo(print())
                .andExpect(status().isUnauthorized());
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
        assertMatch(service.getAll(), asSortedList(USER_COMPARATOR, USER, ADMIN, created), IGNORED_FIELDS);

    }

    @Test
    void registerNotNew() throws Exception {
        User created = getCreated();
        created.setId(USER_ID);

        mockMvc.perform(post(REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(created, created.getPassword())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void registerNameInvalid() throws Exception {
        User created = getCreated();
        created.setName(SHORT_NAME.toString());

        mockMvc.perform(post(REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(created, created.getPassword())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void registerEmailInvalid() throws Exception {
        User created = getCreated();
        created.setEmail(MALFORMED_EMAIL.toString());

        mockMvc.perform(post(REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(created, created.getPassword())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void registerPasswordInvalid() throws Exception {
        User created = getCreated();
        created.setPassword(SHORT_PASSWORD.toString());

        mockMvc.perform(post(REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(created, created.getPassword())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void registerEmailDuplicate() throws Exception {
        User created = getCreated();
        created.setEmail(ADMIN.getEmail());

        mockMvc.perform(post(REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(created, created.getPassword())))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void registerForbidden() throws Exception {
        User created = getCreated();

        mockMvc.perform(post(REST_URL + "/register")
                .with(userHttpBasic(USER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(created, created.getPassword())))
                .andDo(print())
                .andExpect(status().isForbidden());
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

    @Test
    void updateNameInvalid() throws Exception {
        User updated = getUpdated();
        updated.setName(SHORT_NAME.toString());

        mockMvc.perform(put(REST_URL)
                .with(userHttpBasic(USER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateEmailInvalid() throws Exception {
        User updated = getUpdated();
        updated.setEmail(MALFORMED_EMAIL.toString());

        mockMvc.perform(put(REST_URL)
                .with(userHttpBasic(USER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updatePasswordInvalid() throws Exception {
        User updated = getUpdated();
        updated.setPassword(SHORT_PASSWORD.toString());

        mockMvc.perform(put(REST_URL)
                .with(userHttpBasic(USER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateEmailDuplicate() throws Exception {
        User updated = getUpdated();
        updated.setEmail(ADMIN.getEmail());

        mockMvc.perform(put(REST_URL)
                .with(userHttpBasic(USER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void updateUnauthorized() throws Exception {
        User updated = getUpdated();

        mockMvc.perform(put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}