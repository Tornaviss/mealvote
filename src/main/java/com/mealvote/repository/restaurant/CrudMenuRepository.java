package com.mealvote.repository.restaurant;

import com.mealvote.model.restaurant.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CrudMenuRepository extends JpaRepository<Menu, Integer> {
    @Modifying
    @Query("DELETE FROM Menu m " +
            "WHERE m.restaurantId=:restaurantId")
    int delete(@Param("restaurantId") int restaurantId);

    @Query("SELECT m FROM Menu m " +
            "ORDER BY m.date DESC, m.restaurantId")
    List<Menu> getAll();
}
