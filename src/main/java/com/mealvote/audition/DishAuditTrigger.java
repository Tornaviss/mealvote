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

public class DishAuditTrigger implements Trigger {
    private static final Logger LOGG = LoggerFactory.getLogger(DishAuditTrigger.class);

    private int operation;

    @Override
    public void init(Connection connection, String s, String s1, String s2, boolean b, int i) throws SQLException {
        operation = i;
    }

    @Override
    public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException {
        try (PreparedStatement stmt =
                     (operation == UPDATE || operation == INSERT
                             ? prepareSaveStatement(connection, oldRow, newRow)
                             : prepareDeleteStatement(connection, oldRow))) {
            stmt.execute();
        } catch(SQLException e) {
            LOGG.warn("failed to preserve history for dish with id {}", newRow[0]);
        }
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public void remove() throws SQLException {

    }

    private PreparedStatement prepareDeleteStatement(Connection connection, Object[] oldRow) throws SQLException {
        PreparedStatement stmt =  connection.prepareStatement(
                "UPDATE dishes_history " +
                        "SET active=false " +
                        "WHERE id=?");
        stmt.setInt(1, (Integer) oldRow[0]);
        return stmt;
    }

    private PreparedStatement prepareSaveStatement(Connection connection, Object[] oldRow,  Object[] newRow) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO dishes_history (id, name, price, username, action_timestamp, menu_id, action) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)");
        stmt.setInt(1, (Integer) newRow[0]);
        stmt.setString(2, (String) newRow[2]);
        stmt.setInt(3, (Integer) newRow[3]);
        AuthorizedUser authUser = SecurityUtil.safeGet();
        if (authUser == null) {
            stmt.setString(4, "system");
        } else {
            stmt.setString(4, authUser.getUsername());
        }
        stmt.setObject(5, LocalDateTime.now());

        if (operation == UPDATE) {
            stmt.setInt(6, (Integer) oldRow[1]);
            stmt.setString(7, AuditionEvent.UPDATE.toString());
        } else {
            stmt.setInt(6, (Integer) newRow[1]);
            stmt.setString(7, AuditionEvent.CREATE.toString());
        }
        return stmt;
    }
}
