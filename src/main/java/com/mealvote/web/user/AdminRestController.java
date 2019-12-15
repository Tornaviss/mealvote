package com.mealvote.web.user;

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
import java.util.List;

import static com.mealvote.util.ValidationUtil.assureIdConsistent;
import static com.mealvote.util.ValidationUtil.checkNew;
import static com.mealvote.web.user.AdminRestController.REST_URL;

@RestController
@RequestMapping(value = REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminRestController {
    private static final Logger LOGG = LoggerFactory.getLogger(AdminRestController.class);

    static final String REST_URL = "/admin/users";

    private final UserService service;

    @Autowired
    public AdminRestController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<User> getAll() {
        LOGG.info("get all");
        return service.getAll();
    }

    @GetMapping("/{id}")
    public User get(@PathVariable int id) {
        LOGG.info("get user {}", id);
        return service.get(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        service.delete(id);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody User user, @PathVariable int id) {
        LOGG.info("update user {} with {}", id, user);
        assureIdConsistent(user, id);
        service.update(user);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        LOGG.info("create {}", user);
        checkNew(user);
        User created = service.create(user);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @GetMapping("/by")
    public User getByMail(@RequestParam String email) {
        LOGG.info("get by email {}", email);
        return service.getByEmail(email);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void enable(@PathVariable int id, @RequestParam boolean enabled) {
        LOGG.info("set enabled {} for user {}", enabled, id);
        service.enable(id, enabled);
    }

}
