package com.mealvote.model.audition;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "dishes_history", uniqueConstraints =
        @UniqueConstraint(name = "history_id_unique_idx", columnNames = {"history_id", "id"})
)
public class DishHistory extends AbstractHistoryEntity {

    @Column(name = "id")
    private int id;

    @Column(name = "menu_id")
    private int menuId;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private int price;

    public DishHistory() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }
}
