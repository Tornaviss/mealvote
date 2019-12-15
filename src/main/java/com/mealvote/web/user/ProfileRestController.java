package com.mealvote.web.user;

import com.mealvote.model.user.Role;
import com.mealvote.model.user.User;
import com.mealvote.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;

import static com.mealvote.util.ValidationUtil.checkNew;
import static com.mealvote.web.SecurityUtil.*;
import static com.mealvote.web.user.ProfileRestController.REST_URL;

@RestController
@RequestMapping(value = REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProfileRestController {
    private static final Logger LOGG = LoggerFactory.getLogger(ProfileRestController.class);
    static final String REST_URL = "/profile";

    private final UserService service;

    @Autowired
    public ProfileRestController(UserService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public User get() {
        LOGG.info("get for user {}", authUserId());
        return service.get(authUserId());
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete() {
        LOGG.info("delete user {}", authUserId());
        service.delete(authUserId());
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
        LOGG.info("register {}", user);
        checkNew(user);
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        User created = service.create(user);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody User user) {
        LOGG.info("update user {} with {}", authUserId(), user);
        user.setId(authUserId());
        user.setRoles(authUserRoles());
        user.setEnabled(authUserEnabled());
        service.update(user);
    }

}