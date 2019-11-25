package com.mealvote.web.history;

import com.mealvote.model.audition.ChoiceHistory;
import com.mealvote.service.user.ChoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.mealvote.web.history.ChoiceHistoryRestController.REST_URL;

@RestController
@RequestMapping(value = REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class ChoiceHistoryRestController {
    public static final String REST_URL = "/history/choices";

    private final ChoiceService service;

    @Autowired
    public ChoiceHistoryRestController(ChoiceService service) {
        this.service = service;
    }

    @GetMapping
    public List<ChoiceHistory> getAllHistory() {
        return service.getAllHistory();
    }

    @GetMapping(value = "/{userId}")
    public List<ChoiceHistory> getHistory(@PathVariable("userId") int userId) {
        return service.getHistory(userId);
    }

    @PutMapping(value = "/recover")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void recover(@RequestParam("historyId") int historyId) {
        service.recover(historyId);
    }
}
