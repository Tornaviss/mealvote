package com.mealvote.web.user;

import com.mealvote.model.user.Vote;
import com.mealvote.service.user.VoteService;
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
import static com.mealvote.web.user.VoteRestController.REST_URL;

@RestController
@RequestMapping(value = REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class VoteRestController {
    static final String REST_URL = ProfileRestController.REST_URL + "/vote";
    private static final Logger LOGG = LoggerFactory.getLogger(VoteRestController.class);

    private final VoteService service;

    @Autowired
    public VoteRestController(VoteService service) {
        this.service = service;
    }

    @GetMapping
    public Vote get() {
        LOGG.info("get vote for user {}", authUserId());
        return service.get(authUserId());
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestParam int restaurantId) {
        LOGG.info("update vote with restaurant {} for user {}", restaurantId, authUserId());
        service.update(restaurantId, authUserId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Vote create(@RequestParam int restaurantId) {
        LOGG.info("create vote with restaurant {} for user {}", restaurantId, authUserId());
        return service.create(restaurantId, authUserId());
    }

    /**
     * Updates authorized user's vote.restaurant field with restaurant which id is equal to {@param restaurantId}.
     *
     * <p>Only for testing purposes.</p>
     *
     * @param restaurantId restaurant that user wants to vote for
     * @param deadline time until user can change his mind
     * @throws TemporaryUnavailableOperationException if user's related Vote object dateTime value has another date than {@code LocalDate.now())
     *                                   or it's time is less that {@code LocalTime.now()} inclusive
     */
    @PutMapping("/{deadline}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestParam int restaurantId,
                       @PathVariable("deadline") @DateTimeFormat(pattern = DateTimeUtil.TIME_PATTERN) LocalTime deadline) {
        LOGG.info("create vote with restaurant {} for user {}", restaurantId, authUserId());
        service.update(restaurantId, authUserId(), deadline);
    }

}
