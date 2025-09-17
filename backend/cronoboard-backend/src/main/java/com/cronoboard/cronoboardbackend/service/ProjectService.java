package com.cronoboard.cronoboardbackend.service;

import com.cronoboard.cronoboardbackend.dto.project.ProjectCreateRequest;
import com.cronoboard.cronoboardbackend.dto.project.ProjectUpdateRequest;
import com.cronoboard.cronoboardbackend.entity.Project;
import com.cronoboard.cronoboardbackend.entity.Task;
import com.cronoboard.cronoboardbackend.repository.ProjectRepository;
import com.cronoboard.cronoboardbackend.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepo;
    private final TaskRepository taskRepo;

    public ProjectService(ProjectRepository projectRepo, TaskRepository taskRepo) {
        this.projectRepo = projectRepo;
        this.taskRepo = taskRepo;
    }

    // --- CRUD ---

    @Transactional(readOnly = true)
    public List<Project> list(Long userId) {
        return projectRepo.findByUserIdOrderByUpdatedAtDesc(userId);
    }

    @Transactional
    public Project create(Long userId, ProjectCreateRequest req) {
        var p = new Project();
        p.setUserId(userId);
        p.setName(req.getName());
        p.setColorHex(req.getColorHex());
        return projectRepo.save(p);
    }

    @Transactional
    public Project update(Long id, Long userId, ProjectUpdateRequest req) {
        var p = projectRepo.findById(id).orElseThrow(() -> new IllegalStateException("project not found"));
        if (!p.getUserId().equals(userId)) throw new IllegalStateException("forbidden");
        if (req.getName() != null) p.setName(req.getName());
        if (req.getColorHex() != null) p.setColorHex(req.getColorHex());
        return projectRepo.save(p);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        var p = projectRepo.findById(id).orElseThrow(() -> new IllegalStateException("project not found"));
        if (!p.getUserId().equals(userId)) throw new IllegalStateException("forbidden");
        projectRepo.deleteById(id);
    }

    // --- Task binding ---

    @Transactional
    public Task attachTask(Long projectId, Long taskId, Long userId) {
        var p = projectRepo.findById(projectId).orElseThrow(() -> new IllegalStateException("project not found"));
        var t = taskRepo.findById(taskId).orElseThrow(() -> new IllegalStateException("task not found"));
        if (!p.getUserId().equals(userId) || !t.getUserId().equals(userId)) throw new IllegalStateException("forbidden");
        t.setProjectId(p.getId());
        return taskRepo.save(t);
    }

    @Transactional
    public Task detachTask(Long taskId, Long userId) {
        var t = taskRepo.findById(taskId).orElseThrow(() -> new IllegalStateException("task not found"));
        if (!t.getUserId().equals(userId)) throw new IllegalStateException("forbidden");
        t.setProjectId(null);
        return taskRepo.save(t);
    }

    @Transactional
    public Task setParent(Long taskId, Long parentTaskId, Long userId) {
        if (taskId.equals(parentTaskId)) throw new IllegalArgumentException("task cannot be its own parent");
        var t = taskRepo.findById(taskId).orElseThrow(() -> new IllegalStateException("task not found"));
        var parent = taskRepo.findById(parentTaskId).orElseThrow(() -> new IllegalStateException("task not found"));
        if (!t.getUserId().equals(userId) || !parent.getUserId().equals(userId)) throw new IllegalStateException("forbidden");
        t.setParentTaskId(parent.getId());
        // Project isolation: if different projects, align child's project to parent's
        if (parent.getProjectId() != null) {
            t.setProjectId(parent.getProjectId());
        }
        return taskRepo.save(t);
    }

    @Transactional
    public Task clearParent(Long taskId, Long userId) {
        var t = taskRepo.findById(taskId).orElseThrow(() -> new IllegalStateException("task not found"));
        if (!t.getUserId().equals(userId)) throw new IllegalStateException("forbidden");
        t.setParentTaskId(null);
        return taskRepo.save(t);
    }
}


