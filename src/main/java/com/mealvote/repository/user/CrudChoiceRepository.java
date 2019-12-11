package com.mealvote.repository.user;

import com.mealvote.model.user.Choice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface CrudChoiceRepository extends JpaRepository<Choice, Integer> {

    @Query("SELECT c FROM Choice c " +
            "ORDER BY c.dateTime DESC")
    List<Choice> getAll();

}
