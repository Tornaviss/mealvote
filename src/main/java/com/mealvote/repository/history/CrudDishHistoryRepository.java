package com.mealvote.repository.history;

import com.mealvote.model.audition.DishHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CrudDishHistoryRepository extends JpaRepository<DishHistory, Integer> {

    @Query("SELECT d FROM DishHistory d " +
            "WHERE d.id=:dishId " +
            "ORDER BY d.historyId DESC")
    List<DishHistory> getAllByDishId(@Param("dishId") int dishId);

    @Query("SELECT d FROM DishHistory d " +
            "ORDER BY d.historyId DESC")
    List<DishHistory> getAll();

    @Modifying
    @Query("UPDATE DishHistory d " +
            "SET d.active=true " +
            "WHERE d.id=:id")
    int activate(@Param("id") int id);

    @Query("SELECT d FROM DishHistory d " +
            "WHERE d.menuId = :menuId " +
                "AND d.active = TRUE " +
                "AND d.actionTimestamp < :dateTime " +
                "AND d.historyId = " +
                    "(SELECT MAX(dh.historyId) FROM DishHistory dh " +
                    "WHERE dh.id = d.id AND dh.actionTimestamp < :dateTime) " +
            "ORDER BY d.name")
    List<DishHistory> getMenuContentByDateTime(@Param("menuId") int menuId, @Param("dateTime") LocalDateTime dateTime);
}
