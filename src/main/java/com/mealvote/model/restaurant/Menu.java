package com.mealvote.model.restaurant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mealvote.HasId;
import com.mealvote.util.DateTimeUtil;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "menus")
@Access(AccessType.FIELD)
public class Menu implements HasId {

    @Id
    private Integer restaurantId;

    @Column(name = "date", nullable = false, columnDefinition = "date default now()")
    @NotNull
    @JsonFormat(pattern = DateTimeUtil.DATE_PATTERN)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate date = LocalDate.now();

    @Valid
    @NotNull(message = "dishes must not be null")
    @Size(min = 2, message = "menu must contain at least two dishes")
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "menu", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @OrderBy("name")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Dish> dishes;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "restaurant_id", nullable = false, updatable = false, unique = true)
    @JsonIgnore
    private Restaurant restaurant;

    public Menu() {
    }

    public Menu(List<Dish> dishes) {
        this(null, null, dishes);
    }

    public Menu(Integer restaurantId, Restaurant restaurant, List<Dish> dishes) {
        this(restaurantId, restaurant, dishes, LocalDate.now());
    }

    public Menu(Integer restaurantId, Restaurant restaurant, List<Dish> dishes, LocalDate date) {
        this.restaurantId = restaurantId;
        this.restaurant = restaurant;
        this.dishes = dishes;
        this.date = date;
    }

    public Integer getId() {
        return restaurantId;
    }

    public void setId(Integer id) {
        this.restaurantId = id;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public void setDate(LocalDate presentationDate) {
        this.date = presentationDate;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "restaurantId=" + restaurantId +
                ", date=" + date +
                ", dishes=" + dishes +
                '}';
    }
}
