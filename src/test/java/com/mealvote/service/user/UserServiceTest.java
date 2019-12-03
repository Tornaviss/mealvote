package com.mealvote.service.user;

import com.mealvote.model.user.User;
import com.mealvote.util.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Collections;

import static com.mealvote.AssertionUtils.asSortedList;
import static com.mealvote.AssertionUtils.assertMatch;
import static com.mealvote.UserTestData.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(locations = {
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
class UserServiceTest {

    @Autowired
    private UserService service;

    @Test
    void create() {
        User created = getCreated();
        User returned = service.create(created);
        created.setId(returned.getId());
        assertMatch(returned, created, IGNORED_FIELDS);
        assertMatch(service.get(created.getId()), created, IGNORED_FIELDS);
    }

    @Test
    void createNull() {
        assertThrows(IllegalArgumentException.class,
                () -> service.create(null));
    }

    @Test
    void createEmailDuplicate() {
        User newUser = getCreated();
        newUser.setEmail(USER.getEmail());
        assertThrows(DataIntegrityViolationException.class,
                () -> service.create(newUser));
    }

    @Test
    void delete() {
        service.delete(USER_ID);
        assertMatch(service.getAll(), Collections.singletonList(ADMIN), IGNORED_FIELDS);
    }

    @Test
    void deleteNotFound() {
        assertThrows(NotFoundException.class,
                () -> service.delete(1));
    }

    @Test
    void get() {
        assertMatch(service.get(USER_ID), USER, IGNORED_FIELDS);
    }

    @Test
    void getNotFound() {
        assertThrows(NotFoundException.class,
                () -> service.get(1));
    }

    @Test
    void getByEmail() {
        assertMatch(service.getByEmail(USER.getEmail()), USER, IGNORED_FIELDS);
    }

    @Test
    void getByEmailNotFound() {
        assertThrows(NotFoundException.class,
                () -> service.getByEmail("notfound404@absent.gz"));
    }

    @Test
    void getAll() {
        assertMatch(service.getAll(), asSortedList(USER_COMPARATOR, USER, ADMIN), IGNORED_FIELDS);
    }

    @Test
    void update() {
        User updated = getUpdated();
        service.update(updated);
        assertMatch(service.get(USER_ID), updated, IGNORED_FIELDS);
    }

    @Test
    void updateNull() {
        assertThrows(IllegalArgumentException.class,
                () -> service.update(null));
    }

    @Test
    void updateNotFound() {
        User updated = getUpdated();
        updated.setId(1);
        assertThrows(NotFoundException.class, () -> service.update(updated));
    }

    @Test
    void updateEmailDuplicate() {
        User updated = getUpdated();
        updated.setEmail(ADMIN.getEmail());
        assertThrows(DataIntegrityViolationException.class,
                () -> service.update(updated));
    }

    @Test
    void enable() {
        User updated = new User(USER);
        updated.setEnabled(!USER.isEnabled());
        service.enable(USER_ID, !USER.isEnabled());
        assertMatch(service.get(USER_ID), updated, IGNORED_FIELDS);
    }

    @Test
    void enableNotFound() {
        assertThrows(NotFoundException.class,
                () -> service.enable(1, false));
    }
}