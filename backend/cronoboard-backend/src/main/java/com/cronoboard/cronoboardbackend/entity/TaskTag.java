package com.cronoboard.cronoboardbackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "task_tags")
public class TaskTag {
    @EmbeddedId
    private TaskTagKey id;

    public TaskTag() {}
    public TaskTag(Long taskId, Long tagId) { this.id = new TaskTagKey(taskId, tagId); }

    public TaskTagKey getId() { return id; }
    public void setId(TaskTagKey id) { this.id = id; }
}
