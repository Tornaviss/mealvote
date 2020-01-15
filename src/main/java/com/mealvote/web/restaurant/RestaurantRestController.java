package com.mealvote.web.restaurant;

import com.mealvote.model.restaurant.Restaurant;
import com.mealvote.service.restaurant.RestaurantService;
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

import static com.mealvote.util.ValidationUtil.*;
import static com.mealvote.web.SecurityUtil.authUserId;
import static com.mealvote.web.restaurant.RestaurantRestController.REST_URL;

@RestController
@RequestMapping(value = REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class RestaurantRestController {
    static final String REST_URL = "/restaurants";

    private static final Logger LOGG = LoggerFactory.getLogger(RestaurantRestController.class);

    private final RestaurantService service;

    @Autowired
    public RestaurantRestController(RestaurantService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Restaurant get(
            @PathVariable int id,
            @RequestParam(value = "includeVotes", required = false, defaultValue = "false") boolean includeVotes) {
        LOGG.info("get {} " + (includeVotes ? "including" : "not including") + " votes for user {}", id, authUserId());
        return includeVotes ? eraseParentLinks(service.getWithVotes(id)) : service.get(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        LOGG.info("delete {}", id);
        service.delete(id);
    }

    @GetMapping
    public List<Restaurant> getAll() {
        LOGG.info("getAll for user {}", authUserId());
        return service.getAll();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody Restaurant restaurant, @PathVariable int id) {
        LOGG.info("update {} with {}", id, restaurant);
        assureIdConsistent(restaurant, id);
        service.update(restaurant, id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<Restaurant> create(@Valid @RequestBody Restaurant restaurant) {
        LOGG.info("create {}", restaurant);
        checkNew(restaurant);
        Restaurant created = service.create(restaurant);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    private static Restaurant eraseParentLinks(Restaurant restaurant) {
        restaurant.getVotes().forEach(vote -> vote.setRestaurant(null));
        return restaurant;
    }
}
