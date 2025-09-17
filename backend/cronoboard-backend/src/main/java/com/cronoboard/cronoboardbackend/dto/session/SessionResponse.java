package com.cronoboard.cronoboardbackend.dto.session;

import com.cronoboard.cronoboardbackend.entity.SessionState;

import java.time.OffsetDateTime;

public class SessionResponse {
    private Long id;
    private Long userId;
    private Long taskId;
    private Integer plannedMinutes;
    private Integer focusSeconds;
    private OffsetDateTime startedAt;
    private OffsetDateTime endedAt;
    private SessionState state;

    // ctor
    public SessionResponse(Long id, Long userId, Long taskId, Integer plannedMinutes, Integer focusSeconds,
                           OffsetDateTime startedAt, OffsetDateTime endedAt, SessionState state) {
        this.id = id;
        this.userId = userId;
        this.taskId = taskId;
        this.plannedMinutes = plannedMinutes;
        this.focusSeconds = focusSeconds;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.state = state;
    }

    // getters
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public Integer getPlannedMinutes() {
        return plannedMinutes;
    }

    public Integer getFocusSeconds() {
        return focusSeconds;
    }

    public OffsetDateTime getStartedAt() {
        return startedAt;
    }

    public OffsetDateTime getEndedAt() {
        return endedAt;
    }

    public SessionState getState() {
        return state;
    }
}
