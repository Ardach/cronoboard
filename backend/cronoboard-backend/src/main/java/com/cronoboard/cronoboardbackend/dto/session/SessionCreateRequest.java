package com.cronoboard.cronoboardbackend.dto.session;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class SessionCreateRequest {
    @NotNull
    private Long userId;
    private Long taskId;
    @Min(5)
    private Integer plannedMinutes = 25;

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
}
