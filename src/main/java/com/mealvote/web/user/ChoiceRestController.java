package com.mealvote.web.user;

import com.mealvote.model.user.Choice;
import com.mealvote.service.user.ChoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static com.mealvote.web.SecurityUtil.authUserId;
import static com.mealvote.web.user.ChoiceRestController.REST_URL;

@RestController
@RequestMapping(value = REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class ChoiceRestController {
    static final String REST_URL = ProfileRestController.REST_URL + "/choice";

    private final ChoiceService service;

    @Autowired
    public ChoiceRestController(ChoiceService service) {
        this.service = service;
    }

    @GetMapping
    public Choice get() {
        return service.get(authUserId());
    }

    @PutMapping("/{restaurantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable int restaurantId) {
        service.update(restaurantId, authUserId());
    }

    @PostMapping("/{restaurantId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Choice create(@PathVariable int restaurantId) {
        return service.create(restaurantId, authUserId());
    }

}
