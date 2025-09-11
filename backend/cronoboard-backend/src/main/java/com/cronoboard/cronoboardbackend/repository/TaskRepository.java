package com.cronoboard.cronoboardbackend.repository;

import com.cronoboard.cronoboardbackend.entity.Task;
import com.cronoboard.cronoboardbackend.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserIdOrderByUpdatedAtDesc(Long userId);
    List<Task> findByUserIdAndStatusOrderByUpdatedAtDesc(Long userId, TaskStatus status);
}
