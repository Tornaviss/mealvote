package com.mealvote.repository.restaurant;

import com.mealvote.model.restaurant.Restaurant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import static com.mealvote.model.restaurant.Restaurant.*;

public interface CrudRestaurantRepository extends JpaRepository<Restaurant, Integer> {

    @Modifying
    @Query("DELETE FROM Restaurant r " +
            "WHERE r.id=:id")
    int delete(@Param("id") int id);

    @Query(GET_ALL_SORTED)
    List<Restaurant> getAll();

    @EntityGraph(attributePaths = {"menu", "menu.dishes"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query(GET_ALL_SORTED)
    List<Restaurant> getAllWithMenu();

    @EntityGraph(attributePaths = {"votes"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query(GET_ALL_SORTED)
    List<Restaurant> getAllWithVotes();

    @EntityGraph(attributePaths = {"menu", "menu.dishes"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query(GET_BY_ID)
    Restaurant getWithMenu(int id);

    @EntityGraph(attributePaths = {"votes"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query(GET_BY_ID)
    Restaurant getWithVotes(int id);

    @EntityGraph(attributePaths = {"menu", "votes"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query(GET_BY_ID)
    Restaurant getWithMenuAndVotes(int id);
}
