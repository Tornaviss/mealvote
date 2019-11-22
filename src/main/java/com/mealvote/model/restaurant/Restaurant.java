package com.mealvote.model.restaurant;

import com.mealvote.model.AbstractNamedEntity;

import javax.persistence.*;


@Entity
@Table(name = "restaurants", uniqueConstraints =
@UniqueConstraint(name = "CONSTRAINT_B5", columnNames = {"name"}))
public class Restaurant extends AbstractNamedEntity {

    public Restaurant() {
    }

    public Restaurant(String name) {
        this(null, name);
    }

    public Restaurant(Integer id, String name) {
        super(id, name);

    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
