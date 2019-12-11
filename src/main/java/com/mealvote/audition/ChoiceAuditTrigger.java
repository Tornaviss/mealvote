package com.mealvote.audition;

import com.mealvote.AuthorizedUser;
import com.mealvote.web.SecurityUtil;
import org.h2.api.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ChoiceAuditTrigger implements Trigger {
    private static final Logger LOGG = LoggerFactory.getLogger(ChoiceAuditTrigger.class);

    private int operation;

    @Override
    public void init(Connection connection, String s, String s1, String s2, boolean b, int i) throws SQLException {
        operation = i;
    }

    @Override
    public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException {
        try (PreparedStatement stmt =
                     (operation == UPDATE || operation == INSERT
                             ? prepareSaveStatement(connection, newRow)
                             : prepareDeleteStatement(connection, oldRow))) {
            stmt.execute();
        } catch(SQLException e) {
            LOGG.warn("failed to preserve history for choice with id {}", newRow[0]);
        }
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public void remove() throws SQLException {

    }

    private PreparedStatement prepareSaveStatement(Connection connection, Object[] newRow) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO user_choices_history (user_id, restaurant_id, date_time, action, username, action_timestamp) " +
                        "VALUES (?, ?, ?, ?, ?, ?)");
        stmt.setInt(1, (Integer) newRow[0]);
        stmt.setInt(2, (Integer) newRow[1]);
        stmt.setObject(3, newRow[2]);
        if (operation == UPDATE) {
            stmt.setString(4, AuditionEvent.UPDATE.toString());
        } else  {
            stmt.setString(4, AuditionEvent.CREATE.toString());
        }
        AuthorizedUser authUser = SecurityUtil.safeGet();
        if (authUser == null) {
            stmt.setString(5, "system");
        } else {
            stmt.setString(5, authUser.getUsername());
        }
        stmt.setObject(6, LocalDateTime.now());
        return stmt;
    }

    private PreparedStatement prepareDeleteStatement(Connection connection, Object[] oldRow) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("UPDATE user_choices_history " +
                "SET active=false " +
                "WHERE user_id=?");
        stmt.setInt(1, (Integer) oldRow[0]);
        return stmt;
    }
}
