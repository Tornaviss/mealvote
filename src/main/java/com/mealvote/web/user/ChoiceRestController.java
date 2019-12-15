package com.mealvote.web.user;

import com.mealvote.model.user.Choice;
import com.mealvote.service.user.ChoiceService;
import com.mealvote.util.DateTimeUtil;
import com.mealvote.util.exception.TemporaryUnavailableOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

import static com.mealvote.web.SecurityUtil.authUserId;
import static com.mealvote.web.user.ChoiceRestController.REST_URL;

@RestController
@RequestMapping(value = REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class ChoiceRestController {
    static final String REST_URL = ProfileRestController.REST_URL + "/choice";
    private static final Logger LOGG = LoggerFactory.getLogger(ChoiceRestController.class);

    private final ChoiceService service;

    @Autowired
    public ChoiceRestController(ChoiceService service) {
        this.service = service;
    }

    @GetMapping
    public Choice get() {
        LOGG.info("get choice for user {}", authUserId());
        return service.get(authUserId());
    }

    @PutMapping("/{restaurantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable int restaurantId) {
        LOGG.info("update choice with restaurant {} for user {}", restaurantId, authUserId());
        service.update(restaurantId, authUserId());
    }

    @PostMapping("/{restaurantId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Choice create(@PathVariable int restaurantId) {
        LOGG.info("create choice with restaurant {} for user {}", restaurantId, authUserId());
        return service.create(restaurantId, authUserId());
    }

    /**
     * Updates authorized user's choice.restaurant field with restaurant which id is equal to {@param restaurantId}.
     *
     * <p>Only for testing purposes.</p>
     *
     * @param restaurantId restaurant that user wants to choose
     * @param deadline time until user can change his mind
     * @throws TemporaryUnavailableOperationException if user's related Choice object dateTime value has another date than {@code LocalDate.now())
     *                                   or it's time is less that {@code LocalTime.now()} inclusive
     */
    @PutMapping("/{restaurantId}/{deadline}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable int restaurantId,
                       @PathVariable("deadline") @DateTimeFormat(pattern = DateTimeUtil.TIME_PATTERN) LocalTime deadline) {
        LOGG.info("create choice with restaurant {} for user {}", restaurantId, authUserId());
        service.update(restaurantId, authUserId(), deadline);
    }

}
