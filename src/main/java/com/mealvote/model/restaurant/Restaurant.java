package com.mealvote.model.restaurant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mealvote.model.AbstractNamedEntity;
import com.mealvote.model.user.Choice;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "restaurants", uniqueConstraints =
@UniqueConstraint(name = "CONSTRAINT_B5", columnNames = {"name"}))
public class Restaurant extends AbstractNamedEntity {

    @OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY)
    @OrderBy("dateTime DESC")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Choice> choices;

    public Restaurant() {
    }

    public Restaurant(String name) {
        this(null, name);
    }

    public Restaurant(Integer id, String name) {
        this(id, name, null);
    }

    public Restaurant(Integer id, String name, List<Choice> choices) {
        super(id, name);
        this.choices = choices;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
