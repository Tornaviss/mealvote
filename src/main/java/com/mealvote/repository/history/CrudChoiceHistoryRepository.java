package com.mealvote.repository.history;

import com.mealvote.model.audition.ChoiceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CrudChoiceHistoryRepository extends JpaRepository<ChoiceHistory, Integer> {

    @Query("SELECT c FROM ChoiceHistory c " +
            "WHERE c.userId=:userId " +
            "ORDER BY c.historyId DESC")
    List<ChoiceHistory> getAllByUserId(@Param("userId") int userId);

    @Query("SELECT c FROM ChoiceHistory c " +
            "ORDER BY c.userId, c.historyId DESC")
    List<ChoiceHistory> getAll();
}
