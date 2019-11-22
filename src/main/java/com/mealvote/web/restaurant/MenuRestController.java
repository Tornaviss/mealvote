package com.mealvote.web.restaurant;

import com.mealvote.model.restaurant.Menu;
import com.mealvote.service.restaurant.MenuService;
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

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class MenuRestController {
    static final String REST_URL = "/menus";

    private final MenuService service;

    @Autowired
    public MenuRestController(MenuService service) {
        this.service = service;
    }

    @GetMapping(REST_URL)
    public List<Menu> getAll() {
        return service.getAll();
    }

    @GetMapping(REST_URL + "/{restaurantId}")
    public Menu get(@PathVariable int restaurantId) {
        return service.get(restaurantId);
    }

    @DeleteMapping(REST_URL + "/{restaurantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int restaurantId) {
        service.delete(restaurantId);
    }

    @PutMapping(value = REST_URL + "/{restaurantId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody Menu menu, @PathVariable int restaurantId) {
        assureIdConsistent(menu, restaurantId);
        service.update(menu, restaurantId);
    }

    @PostMapping(path = "/restaurants/{restaurantId}/menu", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<Menu> create(@Valid @RequestBody Menu menu, @PathVariable int restaurantId) {
        checkNew(menu);
        Menu created = service.create(menu, restaurantId);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/menus/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

}
