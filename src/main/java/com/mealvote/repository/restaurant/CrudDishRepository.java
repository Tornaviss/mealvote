package com.mealvote.repository.restaurant;

import com.mealvote.model.restaurant.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface CrudDishRepository extends JpaRepository<Dish, Integer> {

    @Modifying
    @Query("DELETE FROM Dish d " +
            "WHERE d.id=:id")
    int delete(@Param("id") int id);

    @Query("SELECT d FROM Dish d " +
            "ORDER BY d.name")
    List<Dish> getAll();

//    @Modifying
//    @Query("UPDATE Dish d SET d.name=:name, d.price=:price WHERE d.id=:id")
//    int update(@Param("name") String name, @Param("price") int price, @Param("id") int id);

//    @Modifying
//    @Query("DELETE FROM Dish d " +
//            "WHERE d.menu.restaurantId=:restaurantId")
//    int deleteAllForMenu(@Param("restaurantId") int restaurantId);
//
//    @Modifying
//    @Transactional
//    @Query("DELETE FROM Dish d " +
//            "WHERE d.menu.restaurantId=:restaurantId " +
//            "AND d.id NOT IN :ids")
//    int deleteAllForMenuExcept(@Param("restaurantId") int restaurantId, @Param("ids") Collection<Integer> idsToKeep);

    @Query("SELECT d FROM Dish d WHERE d.menu.restaurantId=:restaurantId ORDER BY d.name")
    List<Dish> getAllForMenu(@Param("restaurantId") int restaurantId);
}
