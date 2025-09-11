package com.cronoboard.cronoboardbackend.controller;

import com.cronoboard.cronoboardbackend.dto.task.TaskCreateRequest;
import com.cronoboard.cronoboardbackend.dto.task.TaskUpdateRequest;
import com.cronoboard.cronoboardbackend.entity.Task;
import com.cronoboard.cronoboardbackend.entity.TaskStatus;
import com.cronoboard.cronoboardbackend.repository.TaskRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository repo;

    public TaskController(TaskRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Task> list(@RequestParam Long userId,
                           @RequestParam(required = false) TaskStatus status) {
        if (status == null) {
            return repo.findByUserIdOrderByUpdatedAtDesc(userId);
        }
        return repo.findByUserIdAndStatusOrderByUpdatedAtDesc(userId, status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> get(@PathVariable Long id) {
        return repo.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Task> create(@Valid @RequestBody TaskCreateRequest req) {
        Task t = new Task();
        t.setUserId(req.getUserId());
        t.setProjectId(req.getProjectId());
        t.setParentTaskId(req.getParentTaskId());
        t.setTitle(req.getTitle());
        t.setDescription(req.getDescription());
        t.setStatus(req.getStatus());
        t.setPriority(req.getPriority());
        t.setDueDate(req.getDueDate());
        t.setOrderIndex(req.getOrderIndex());
        t.setEstimatedPomodoros(req.getEstimatedPomodoros());
        return ResponseEntity.ok(repo.save(t));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable Long id,
                                       @Valid @RequestBody TaskUpdateRequest req) {
        return repo.findById(id).map(t -> {
            if (req.getTitle() != null) t.setTitle(req.getTitle());
            if (req.getDescription() != null) t.setDescription(req.getDescription());
            if (req.getStatus() != null) t.setStatus(req.getStatus());
            if (req.getPriority() != null) t.setPriority(req.getPriority());
            if (req.getDueDate() != null) t.setDueDate(req.getDueDate());
            if (req.getOrderIndex() != null) t.setOrderIndex(req.getOrderIndex());
            if (req.getEstimatedPomodoros() != null) t.setEstimatedPomodoros(req.getEstimatedPomodoros());
            if (req.getProjectId() != null) t.setProjectId(req.getProjectId());
            if (req.getParentTaskId() != null) t.setParentTaskId(req.getParentTaskId());
            return ResponseEntity.ok(repo.save(t));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id); // hard delete
        return ResponseEntity.noContent().build();
    }
}
