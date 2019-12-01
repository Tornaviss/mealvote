package com.mealvote.model.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mealvote.model.restaurant.Restaurant;
import com.mealvote.util.DateTimeUtil;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Access(AccessType.FIELD)
@Table(name = "user_choices")
public class Choice {

    @Id
    private Integer userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false, updatable = false, unique = true)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private Restaurant restaurant;

    @Column(name = "date_time", nullable = false, columnDefinition = "timestamp(0) default now()")
    @JsonFormat(pattern = DateTimeUtil.DATE_TIME_PATTERN)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime dateTime = LocalDateTime.now();

    public Choice() {
    }

    public Choice(User user, Restaurant restaurant) {
        this(null, user, restaurant, LocalDateTime.now());
    }

    public Choice(Integer userId, User user, Restaurant restaurant, LocalDateTime dateTime) {
        this.userId = userId;
        this.user = user;
        this.restaurant = restaurant;
        this.dateTime = dateTime;
    }
//
//    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
//    public Integer getUserId() {
//        return user == null ? null : user.getId();
//    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "Choice{" +
                "userId=" + userId +
//                ", restaurant=" + restaurant +
                ", dateTime=" + dateTime +
                '}';
    }
}
