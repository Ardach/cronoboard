package com.cronoboard.cronoboardbackend.controller;

import com.cronoboard.cronoboardbackend.dto.project.ProjectCreateRequest;
import com.cronoboard.cronoboardbackend.dto.project.ProjectResponse;
import com.cronoboard.cronoboardbackend.dto.project.ProjectUpdateRequest;
import com.cronoboard.cronoboardbackend.dto.task.TaskResponse;
import com.cronoboard.cronoboardbackend.entity.Project;
import com.cronoboard.cronoboardbackend.entity.Task;
import com.cronoboard.cronoboardbackend.security.SecurityUtil;
import com.cronoboard.cronoboardbackend.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService service;
    private final SecurityUtil securityUtil;

    public ProjectController(ProjectService service, SecurityUtil securityUtil) {
        this.service = service;
        this.securityUtil = securityUtil;
    }

    // --- CRUD ---

    @GetMapping
    public List<ProjectResponse> list() {
        Long userId = securityUtil.getCurrentUserId();
        return service.list(userId).stream().map(this::toResponse).toList();
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectCreateRequest req) {
        Long userId = securityUtil.getCurrentUserId();
        Project p = service.create(userId, req);
        return ResponseEntity.status(201).body(toResponse(p));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponse> update(@PathVariable Long id, @Valid @RequestBody ProjectUpdateRequest req) {
        Long userId = securityUtil.getCurrentUserId();
        try {
            Project p = service.update(id, userId, req);
            return ResponseEntity.ok(toResponse(p));
        } catch (IllegalStateException ex) {
            return mapError(ex);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Long userId = securityUtil.getCurrentUserId();
        try {
            service.delete(id, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException ex) {
            if ("project not found".equals(ex.getMessage())) return ResponseEntity.notFound().build();
            if ("forbidden".equals(ex.getMessage())) return ResponseEntity.status(403).build();
            return ResponseEntity.badRequest().build();
        }
    }

    // --- Task relations ---

    @PostMapping("/{projectId}/tasks/{taskId}")
    public ResponseEntity<TaskResponse> attachTask(@PathVariable Long projectId, @PathVariable Long taskId) {
        Long userId = securityUtil.getCurrentUserId();
        try {
            Task t = service.attachTask(projectId, taskId, userId);
            return ResponseEntity.ok(toTaskResponse(t));
        } catch (IllegalStateException ex) {
            return mapTaskError(ex);
        }
    }

    @DeleteMapping("/tasks/{taskId}/project")
    public ResponseEntity<TaskResponse> detachTask(@PathVariable Long taskId) {
        Long userId = securityUtil.getCurrentUserId();
        try {
            Task t = service.detachTask(taskId, userId);
            return ResponseEntity.ok(toTaskResponse(t));
        } catch (IllegalStateException ex) {
            return mapTaskError(ex);
        }
    }

    @PostMapping("/tasks/{taskId}/parent/{parentTaskId}")
    public ResponseEntity<TaskResponse> setParent(@PathVariable Long taskId, @PathVariable Long parentTaskId) {
        Long userId = securityUtil.getCurrentUserId();
        try {
            Task t = service.setParent(taskId, parentTaskId, userId);
            return ResponseEntity.ok(toTaskResponse(t));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException ex) {
            return mapTaskError(ex);
        }
    }

    @DeleteMapping("/tasks/{taskId}/parent")
    public ResponseEntity<TaskResponse> clearParent(@PathVariable Long taskId) {
        Long userId = securityUtil.getCurrentUserId();
        try {
            Task t = service.clearParent(taskId, userId);
            return ResponseEntity.ok(toTaskResponse(t));
        } catch (IllegalStateException ex) {
            return mapTaskError(ex);
        }
    }

    // --- Mappers ---

    private ProjectResponse toResponse(Project p) {
        var r = new ProjectResponse();
        r.setId(p.getId());
        r.setUserId(p.getUserId());
        r.setName(p.getName());
        r.setColorHex(p.getColorHex());
        r.setArchivedAt(p.getArchivedAt());
        r.setCreatedAt(p.getCreatedAt());
        r.setUpdatedAt(p.getUpdatedAt());
        return r;
    }

    private TaskResponse toTaskResponse(Task t) {
        var r = new TaskResponse();
        r.setId(t.getId());
        r.setUserId(t.getUserId());
        r.setProjectId(t.getProjectId());
        r.setParentTaskId(t.getParentTaskId());
        r.setTitle(t.getTitle());
        r.setDescription(t.getDescription());
        r.setStatus(t.getStatus().name());
        r.setPriority(t.getPriority());
        r.setDueDate(t.getDueDate());
        r.setOrderIndex(t.getOrderIndex());
        r.setEstimatedPomodoros(t.getEstimatedPomodoros());
        r.setTotalFocusSeconds(t.getTotalFocusSeconds());
        r.setCompletedSessions(t.getCompletedSessions());
        return r;
    }

    private ResponseEntity<ProjectResponse> mapError(IllegalStateException ex) {
        if ("project not found".equals(ex.getMessage())) return ResponseEntity.notFound().build();
        if ("forbidden".equals(ex.getMessage())) return ResponseEntity.status(403).build();
        return ResponseEntity.badRequest().build();
    }

    private ResponseEntity<TaskResponse> mapTaskError(IllegalStateException ex) {
        String msg = ex.getMessage();
        if ("task not found".equals(msg)) return ResponseEntity.notFound().build();
        if ("forbidden".equals(msg)) return ResponseEntity.status(403).build();
        return ResponseEntity.badRequest().build();
    }
}


