package com.mealvote.web.history;

import com.mealvote.model.audition.DishHistory;
import com.mealvote.service.restaurant.DishService;
import com.mealvote.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.mealvote.web.history.DishHistoryRestController.REST_URL;

@RestController
@RequestMapping(value = REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class DishHistoryRestController {
    public static final String REST_URL = "/history/dishes";

    private final DishService service;

    @Autowired
    public DishHistoryRestController(DishService service) {
        this.service = service;
    }

    @GetMapping
    public List<DishHistory> getAllHistory() {
        return service.getAllHistory();
    }

    @GetMapping("/{dishId}")
    public List<DishHistory> getHistory(@PathVariable("dishId") int dishId) {
        return service.getHistory(dishId);
    }

    @PutMapping(value = "/recover")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void recover(@RequestParam("historyId") int historyId) {
        service.recover(historyId);
    }

    @GetMapping(value = "/by")
    public List<DishHistory> getMenuContentByDateTime(
            @RequestParam("menuId") int menuId,
            @RequestParam("dateTime") @DateTimeFormat(pattern = DateTimeUtil.DATE_TIME_PATTERN) LocalDateTime dateTime
    ) {
        return service.getMenuContentByDateTime(menuId, dateTime);
    }
}
