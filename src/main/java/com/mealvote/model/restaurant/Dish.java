package com.mealvote.model.restaurant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mealvote.model.AbstractNamedEntity;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "dishes", uniqueConstraints =
@UniqueConstraint(name = "dish_name_menu_unique_idx", columnNames = {"name", "menu_id"}))
public class Dish extends AbstractNamedEntity {

    @Column(name = "price", nullable = false)
    @Min(value = 1, message = "price of dish must be bigger than {value}")
    @NotNull(message = "price must not be null")
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false, updatable = false)
    @JsonIgnore
    private Menu menu;

    public Dish() {
    }

    public Dish(String name, Integer price) {
        this(null, name, price, null);
    }

    public Dish(Integer id, String name, Integer price, Menu menu) {
        super(id, name);
        this.price = price;
        this.menu = menu;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }

}
