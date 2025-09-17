package com.cronoboard.cronoboardbackend.dto.task;

public class TaskResponse {
    private Long id;
    private Long userId;
    private Long projectId;
    private Long parentTaskId;
    private String title;
    private String description;
    private String status;         // TaskStatus name()
    private short priority;
    private java.sql.Date dueDate;
    private java.math.BigDecimal orderIndex;
    private int estimatedPomodoros;

    private int totalFocusSeconds;
    private int completedSessions;
    private double actualPomodoros; // türetilmiş: totalFocusSeconds / 1500.0

    // --- getters & setters ---
    // (tüm alanlar için)
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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(Long parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public short getPriority() {
        return priority;
    }

    public void setPriority(short priority) {
        this.priority = priority;
    }

    public java.sql.Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(java.sql.Date dueDate) {
        this.dueDate = dueDate;
    }

    public java.math.BigDecimal getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(java.math.BigDecimal orderIndex) {
        this.orderIndex = orderIndex;
    }

    public int getEstimatedPomodoros() {
        return estimatedPomodoros;
    }

    public void setEstimatedPomodoros(int estimatedPomodoros) {
        this.estimatedPomodoros = estimatedPomodoros;
    }

    public int getTotalFocusSeconds() {
        return totalFocusSeconds;
    }

    public void setTotalFocusSeconds(int totalFocusSeconds) {
        this.totalFocusSeconds = totalFocusSeconds;
    }

    public int getCompletedSessions() {
        return completedSessions;
    }

    public void setCompletedSessions(int completedSessions) {
        this.completedSessions = completedSessions;
    }

    public double getActualPomodoros() {
        return actualPomodoros;
    }

    public void setActualPomodoros(double actualPomodoros) {
        this.actualPomodoros = actualPomodoros;
    }
}
