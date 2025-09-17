package com.cronoboard.cronoboardbackend.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "pomodoro_sessions")
public class PomodoroSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "task_id")
    private Long taskId; // nullable

    @Column(name = "planned_minutes", nullable = false)
    private Integer plannedMinutes = 25;

    @Column(name = "focus_seconds", nullable = false)
    private Integer focusSeconds = 0;

    @Column(name = "started_at", nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime startedAt;

    @Column(name = "ended_at", columnDefinition = "timestamptz")
    private OffsetDateTime endedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private SessionState state = SessionState.running;

    @Column(name = "notes")
    private String notes;

    // getters & setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getPlannedMinutes() {
        return plannedMinutes;
    }

    public void setPlannedMinutes(Integer plannedMinutes) {
        this.plannedMinutes = plannedMinutes;
    }

    public Integer getFocusSeconds() {
        return focusSeconds;
    }

    public void setFocusSeconds(Integer focusSeconds) {
        this.focusSeconds = focusSeconds;
    }

    public OffsetDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(OffsetDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public OffsetDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(OffsetDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public SessionState getState() {
        return state;
    }

    public void setState(SessionState state) {
        this.state = state;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
