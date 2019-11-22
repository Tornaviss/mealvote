package com.mealvote.repository.user;

import com.mealvote.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface CrudUserRepository extends JpaRepository<User, Integer> {
    @Modifying
    @Query("DELETE FROM User u " +
            "WHERE u.id=:id")
    int delete(@Param("id") int id);
    
    User getByEmail(String email);

    @Modifying
    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.roles " +
            "ORDER BY u.name, u.email")
    List<User> getAll();
}
