package com.mealvote.repository.user;

import com.mealvote.model.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CrudUserRepository extends JpaRepository<User, Integer> {
    @Modifying
    @Query("DELETE FROM User u " +
            "WHERE u.id=:id")
    int delete(@Param("id") int id);

    @EntityGraph(attributePaths = {"roles"}, type = EntityGraph.EntityGraphType.LOAD)
    User getByEmail(String email);

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.roles " +
            "ORDER BY u.name, u.email")
    List<User> getAll();
}
