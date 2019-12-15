package com.mealvote.web.restaurant;

import com.mealvote.model.restaurant.Dish;
import com.mealvote.service.restaurant.DishService;
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
import static com.mealvote.web.SecurityUtil.authUserId;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class DishRestController {
    private static final Logger LOGG = LoggerFactory.getLogger(DishRestController.class);

    static final String REST_URL = "/dishes";
    static final String POST_REST_URL = "/menus/{menuId}/dishes";

    private final DishService service;

    @Autowired
    public DishRestController(DishService service) {
        this.service = service;
    }

    @GetMapping(REST_URL)
    public List<Dish> getAll() {
        LOGG.info("get all for user {}", authUserId());
        return service.getAll();
    }

    @GetMapping(REST_URL + "/{id}")
    public Dish get(@PathVariable int id) {
        LOGG.info("get {} for user {}", id, authUserId());
        return service.get(id);
    }

    @DeleteMapping(REST_URL + "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        LOGG.info("delete dish {}", id);
        service.delete(id);
    }

    @PutMapping(value = REST_URL + "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody Dish dish, @PathVariable int id) {
        LOGG.info("update {} with {}", id, dish);
        assureIdConsistent(dish, id);
        service.update(dish);
    }

    @PostMapping(value = POST_REST_URL, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Dish> create(@Valid @RequestBody Dish dish, @PathVariable int menuId) {
        LOGG.info("create {} for menu {}", dish, menuId);
        checkNew(dish);
        Dish created = service.create(dish, menuId);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/dishes/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }
}