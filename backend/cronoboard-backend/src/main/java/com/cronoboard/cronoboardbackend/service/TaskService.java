package com.cronoboard.cronoboardbackend.service;

import com.cronoboard.cronoboardbackend.entity.Task;
import com.cronoboard.cronoboardbackend.entity.TaskStatus;
import com.cronoboard.cronoboardbackend.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    private final TaskRepository taskRepo;

    public TaskService(TaskRepository taskRepo) {
        this.taskRepo = taskRepo;
    }

    @Transactional(readOnly = true)
    public Task requireStartable(Long taskId, Long userId) {
        Task t = taskRepo.findById(taskId).orElseThrow(() -> new IllegalStateException("task not found"));
        if (!t.getUserId().equals(userId)) {
            throw new IllegalStateException("forbidden");
        }
        if (t.getStatus() == TaskStatus.done || t.getStatus() == TaskStatus.archived) {
            throw new IllegalStateException("task not startable");
        }
        return t;
    }

    // TaskService.java
    @Transactional
    public void applyFocusProgress(Long taskId, Long userId, int focusSeconds, boolean completedPomodoro) {
        taskRepo.findById(taskId).ifPresent(t -> {
            if (!t.getUserId().equals(userId)) throw new IllegalStateException("forbidden");
            t.setTotalFocusSeconds(t.getTotalFocusSeconds() + Math.max(focusSeconds, 0));
            if (completedPomodoro) {
                t.setCompletedSessions(t.getCompletedSessions() + 1);
            }
            taskRepo.save(t);
        });
    }

    @Transactional
    public void markInProgressIfTodo(Task task) {
        if (task.getStatus() == TaskStatus.todo) {
            task.setStatus(TaskStatus.in_progress);
            taskRepo.save(task);
        }
    }
}
