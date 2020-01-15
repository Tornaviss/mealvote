package com.mealvote.repository.user;

import com.mealvote.model.user.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CrudVoteRepository extends JpaRepository<Vote, Integer> {

    @Query("SELECT v FROM Vote v " +
            "ORDER BY v.dateTime DESC")
    List<Vote> getAll();

}
