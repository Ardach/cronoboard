package com.cronoboard.cronoboardbackend.controller;

import com.cronoboard.cronoboardbackend.entity.TaskTag;
import com.cronoboard.cronoboardbackend.entity.TaskTagKey;
import com.cronoboard.cronoboardbackend.repository.TaskTagRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks/{taskId}/tags")
public class TaskTagController {

    private final TaskTagRepository repo;

    public TaskTagController(TaskTagRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public ResponseEntity<Void> attach(@PathVariable Long taskId, @RequestParam Long tagId) {
        repo.save(new TaskTag(taskId, tagId));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> detach(@PathVariable Long taskId, @PathVariable Long tagId) {
        var key = new TaskTagKey(taskId, tagId);
        if (!repo.existsById(key)) return ResponseEntity.notFound().build();
        repo.deleteById(key);
        return ResponseEntity.noContent().build();
    }
}
