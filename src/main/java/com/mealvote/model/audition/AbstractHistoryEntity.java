package com.mealvote.model.audition;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mealvote.util.DateTimeUtil;

import javax.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class AbstractHistoryEntity {

    @Id
    @Column(name = "history_id")
    @SequenceGenerator(name = "global_generator", sequenceName = "history_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_generator")
    private int historyId;

    @Column(name = "action")
    private String action;

    @Column(name = "action_timestamp")
    @JsonFormat(pattern = DateTimeUtil.DATE_TIME_PATTERN)
    private LocalDateTime actionTimestamp;

    @Column(name = "username")
    private String userName;

    @Column(name = "active")
    private boolean active;

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getActionTimestamp() {
        return actionTimestamp;
    }

    public void setActionTimestamp(LocalDateTime actionTimestamp) {
        this.actionTimestamp = actionTimestamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
