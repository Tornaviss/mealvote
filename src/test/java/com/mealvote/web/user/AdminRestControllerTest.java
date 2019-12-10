package com.mealvote.web.user;

import com.mealvote.model.user.User;
import com.mealvote.service.user.UserService;
import com.mealvote.web.AbstractRestControllerTest;
import org.junit.jupiter.api.Assertions;
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
import static com.mealvote.web.user.AdminRestController.REST_URL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminRestControllerTest extends AbstractRestControllerTest {

    @Autowired
    private UserService service;

    @Test
    void getAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(asSortedList(USER_COMPARATOR, USER, ADMIN), User.class, IGNORED_FIELDS));
    }

    @Test
    void getAllForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "/" + USER_ID)
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(USER, User.class, IGNORED_FIELDS));
    }

    @Test
    void getNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "/" + 1)
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "/" + USER_ID)
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + "/" + USER_ID)
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertMatch(service.getAll(), Collections.singletonList(ADMIN), IGNORED_FIELDS);
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
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + "/" + USER_ID)
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void create() throws Exception {
        User created = getCreated();

        ResultActions action = mockMvc.perform(post(REST_URL)
                .with(userHttpBasic(ADMIN))
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
    void createNotNew() throws Exception {
        User created = getCreated();
        created.setId(USER_ID);
        mockMvc.perform(post(REST_URL)
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(created, created.getPassword())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createNameInvalid() throws Exception {
        User created = getCreated();
        created.setName(SHORT_NAME.toString());

        mockMvc.perform(post(REST_URL)
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(created, created.getPassword())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createEmailInvalid() throws Exception {
        User created = getCreated();
        created.setEmail(MALFORMED_EMAIL.toString());

        mockMvc.perform(post(REST_URL)
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(created, created.getPassword())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createPasswordInvalid() throws Exception {
        User created = getCreated();
        created.setPassword(SHORT_PASSWORD.toString());

        mockMvc.perform(post(REST_URL)
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(created, created.getPassword())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createEmailDuplicate() throws Exception {
        User created = getCreated();
        created.setEmail(USER.getEmail());

        mockMvc.perform(post(REST_URL)
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(created, created.getPassword())))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void createForbidden() throws Exception {
        User created = getCreated();

        mockMvc.perform(post(REST_URL)
                .with(userHttpBasic(USER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(created, created.getPassword())))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void update() throws Exception {
        User updated = getUpdated();

        mockMvc.perform(put(REST_URL + "/" + updated.getId())
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertMatch(service.getByEmail(updated.getEmail()), updated, IGNORED_FIELDS);
        assertMatch(service.getAll(), asSortedList(USER_COMPARATOR, updated, ADMIN), IGNORED_FIELDS);
    }

    @Test
    void updateNotFound() throws Exception {
        User updated = getUpdated();
        updated.setId(1);

        mockMvc.perform(put(REST_URL + "/" + updated.getId())
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void updateIdNotMatch() throws Exception {
        User updated = getUpdated();
        updated.setId(1);

        mockMvc.perform(put(REST_URL + "/" + USER_ID)
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateEmailInvalid() throws Exception {
        User updated = getUpdated();
        updated.setEmail(MALFORMED_EMAIL.toString());

        mockMvc.perform(put(REST_URL + "/" + updated.getId())
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateNameInvalid() throws Exception {
        User updated = getUpdated();
        updated.setName(SHORT_NAME.toString());

        mockMvc.perform(put(REST_URL + "/" + updated.getId())
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updatePasswordInvalid() throws Exception {
        User updated = getUpdated();
        updated.setPassword(SHORT_PASSWORD.toString());

        mockMvc.perform(put(REST_URL + "/" + updated.getId())
                .with(userHttpBasic(ADMIN))
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

        mockMvc.perform(put(REST_URL + "/" + updated.getId())
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void updateForbidden() throws Exception {
        User updated = getUpdated();

        mockMvc.perform(put(REST_URL + "/" + updated.getId())
                .with(userHttpBasic(USER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void getByEmail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get(REST_URL + "/by").param("email", USER.getEmail())
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(USER, User.class, IGNORED_FIELDS));
    }

    @Test
    void getByEmailNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get(REST_URL + "/by").param("email", "notfound404@absent.gz")
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getByEmailForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get(REST_URL + "/by").param("email", USER.getEmail())
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void enable() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .patch(REST_URL + "/" + USER_ID).param("enabled", "false")
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isNoContent());

        Assertions.assertNotEquals(true, service.get(USER_ID).isEnabled());
    }

    @Test
    void enableNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .patch(REST_URL + "/" + 1).param("enabled", "false")
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void enableForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .patch(REST_URL + "/" + USER_ID).param("enabled", "false")
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}