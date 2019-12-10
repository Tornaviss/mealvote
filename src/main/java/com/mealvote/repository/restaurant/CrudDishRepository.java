package com.mealvote.repository.restaurant;

import com.mealvote.model.restaurant.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CrudDishRepository extends JpaRepository<Dish, Integer> {

    @Modifying
    @Query("DELETE FROM Dish d " +
            "WHERE d.id=:id")
    int delete(@Param("id") int id);

    @Query("SELECT d FROM Dish d " +
            "ORDER BY d.name")
    List<Dish> getAll();

    @Query("SELECT d FROM Dish d WHERE d.menu.restaurantId=:restaurantId ORDER BY d.name")
    List<Dish> getAllForMenu(@Param("restaurantId") int restaurantId);
}
