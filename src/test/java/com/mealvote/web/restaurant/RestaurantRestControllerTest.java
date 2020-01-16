package com.mealvote.web.restaurant;

import com.mealvote.MenuTestData;
import com.mealvote.VoteTestData;
import com.mealvote.model.restaurant.Menu;
import com.mealvote.model.restaurant.Restaurant;
import com.mealvote.repository.restaurant.CrudRestaurantRepository;
import com.mealvote.web.AbstractRestControllerTest;
import com.mealvote.web.json.JsonUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.mealvote.AssertionUtils.*;
import static com.mealvote.JsonParseUtils.readListFromJsonMvcResult;
import static com.mealvote.MenuTestData.DOMINOS_MENU;
import static com.mealvote.MenuTestData.VEGANO_MENU;
import static com.mealvote.MenuTestData.MENU_COMPARATOR;
import static com.mealvote.MenuTestData.MENU_IGNORED_FIELDS;
import static com.mealvote.VoteTestData.USER_VOTE;
import static com.mealvote.JsonParseUtils.readFromJson;
import static com.mealvote.RestaurantTestData.*;
import static com.mealvote.UserTestData.ADMIN;
import static com.mealvote.UserTestData.USER;
import static com.mealvote.UserTestData.userHttpBasic;
import static com.mealvote.web.restaurant.RestaurantRestController.REST_URL;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    void getNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "/" + 1)
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getWithVotes() throws Exception {
        ResultActions action = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "/" + DOMINOS_ID)
                .param("includeVotes", "true")
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(contentJson(DOMINOS, Restaurant.class, IGNORED_FIELDS));

        Restaurant returned = readFromJson(action, Restaurant.class);
        assertMatch(returned.getVotes(), Collections.singletonList(USER_VOTE), VoteTestData.IGNORED_FIELDS);
    }

    @Test
    void getWithVotesNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "/" + 1)
                .param("includeVotes", "true")
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getWithVotesUnauthorised() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "/" + 1)
                .param("includeVotes", "true"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getWithMenu() throws Exception {
        ResultActions action = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "/" + DOMINOS_ID)
                .param("includeMenu", "true")
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(contentJson(DOMINOS, Restaurant.class, IGNORED_FIELDS));

        Restaurant returned = readFromJson(action, Restaurant.class);
        Menu returnedMenu = returned.getMenu();
        assertMatch(returnedMenu, DOMINOS_MENU, MenuTestData.MENU_IGNORED_FIELDS);
        assertMatch(returnedMenu.getDishes(), DOMINOS_MENU.getDishes(), MenuTestData.DISH_IGNORED_FIELDS);
    }

    @Test
    void getWithMenuAndVotes() throws Exception {
        ResultActions action = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "/" + DOMINOS_ID)
                .param("includeVotes", "true")
                .param("includeMenu", "true")
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(contentJson(DOMINOS, Restaurant.class, IGNORED_FIELDS));

        Restaurant returned = readFromJson(action, Restaurant.class);
        assertMatch(returned.getVotes(), Collections.singletonList(USER_VOTE), VoteTestData.IGNORED_FIELDS);

        Menu returnedMenu = returned.getMenu();
        assertMatch(returnedMenu, DOMINOS_MENU, MenuTestData.MENU_IGNORED_FIELDS);
        assertMatch(returnedMenu.getDishes(), DOMINOS_MENU.getDishes(), MenuTestData.DISH_IGNORED_FIELDS);
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
    void getAllWithMenus() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param("includeMenu", "true")
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(contentJson(
                        asSortedList(COMPARATOR, DOMINOS, MAFIA, VEGANO), Restaurant.class, IGNORED_FIELDS))
                .andReturn();

        List<Restaurant> returned = readListFromJsonMvcResult(result, Restaurant.class);
        List<Menu> returnedMenus = returned.stream()
                .filter(x -> x.getMenu() != null)
                .map(Restaurant::getMenu)
                .sorted(MENU_COMPARATOR)
                .collect(Collectors.toList());

        assertMatch(returnedMenus, asSortedList(MENU_COMPARATOR, DOMINOS_MENU, VEGANO_MENU), MENU_IGNORED_FIELDS);
        returnedMenus.forEach(x -> Assertions.assertNotNull(x.getDishes()));
    }

    @Test
    void getAllWithVotes() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param("includeVotes", "true")
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(contentJson(
                        asSortedList(COMPARATOR, DOMINOS, MAFIA, VEGANO), Restaurant.class, IGNORED_FIELDS))
                .andReturn();

        List<Restaurant> returned = readListFromJsonMvcResult(result, Restaurant.class);
        assertNotNull(returned.stream().filter(x -> x.getVotes() != null).findAny().orElse(null));

    }

    @Test
    void getAllWithMenusAndVotes() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param("includeMenu", "true")
                .param("includeVotes", "true")
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(contentJson(
                        asSortedList(COMPARATOR, DOMINOS, MAFIA, VEGANO), Restaurant.class, IGNORED_FIELDS))
                .andReturn();

        List<Restaurant> returned = readListFromJsonMvcResult(result, Restaurant.class);
        assertNotNull(returned.stream().filter(x -> x.getVotes() != null).findAny().orElse(null));

        List<Menu> returnedMenus = returned.stream()
                .filter(x -> x.getMenu() != null)
                .map(Restaurant::getMenu)
                .sorted(MENU_COMPARATOR)
                .collect(Collectors.toList());

        assertMatch(returnedMenus, asSortedList(MENU_COMPARATOR, DOMINOS_MENU, VEGANO_MENU), MENU_IGNORED_FIELDS);
        returnedMenus.forEach(x -> Assertions.assertNotNull(x.getDishes()));
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

}
