package com.mealvote.model.restaurant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mealvote.model.AbstractNamedEntity;
import com.mealvote.model.user.Vote;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "restaurants", uniqueConstraints =
@UniqueConstraint(name = "CONSTRAINT_B5", columnNames = {"name"}))
public class Restaurant extends AbstractNamedEntity {

    public static final String GET_BY_ID = "SELECT r FROM Restaurant r WHERE r.id=?1";
    public static final String GET_ALL_SORTED = "SELECT r FROM Restaurant r ORDER BY r.name";

    @OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY)
    @OrderBy("dateTime DESC")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Vote> votes;

    @OneToOne(mappedBy = "restaurant", fetch = FetchType.LAZY, optional = false)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Menu menu;

    public Restaurant() {
    }

    public Restaurant(String name) {
        this(null, name);
    }

    public Restaurant(Integer id, String name) {
        this(id, name, null);
    }

    public Restaurant(Integer id, String name, List<Vote> votes) {
        super(id, name);
        this.votes = votes;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
