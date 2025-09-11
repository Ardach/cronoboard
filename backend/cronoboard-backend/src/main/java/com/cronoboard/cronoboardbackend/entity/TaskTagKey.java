package com.cronoboard.cronoboardbackend.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TaskTagKey implements Serializable {
    private Long taskId;
    private Long tagId;

    public TaskTagKey() {
    }

    public TaskTagKey(Long taskId, Long tagId) {
        this.taskId = taskId;
        this.tagId = tagId;
    }

    // getters/setters, equals & hashCode
    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskTagKey that)) return false;
        return Objects.equals(taskId, that.taskId) && Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, tagId);
    }
}
