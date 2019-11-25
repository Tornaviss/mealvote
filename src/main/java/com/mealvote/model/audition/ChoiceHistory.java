package com.mealvote.model.audition;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mealvote.util.DateTimeUtil;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_choices_history")
@Access(AccessType.FIELD)
public class ChoiceHistory extends AbstractHistoryEntity {

    @Column(name = "user_id")
    private int userId;

    @Column(name = "restaurant_id")
    private int restaurantId;

    @Column(name = "date_time")
    @JsonFormat(pattern = DateTimeUtil.DATE_TIME_PATTERN)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime dateTime;

    public ChoiceHistory() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
