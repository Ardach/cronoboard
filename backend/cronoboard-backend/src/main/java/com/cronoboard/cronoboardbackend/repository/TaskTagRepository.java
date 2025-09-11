package com.cronoboard.cronoboardbackend.repository;

import com.cronoboard.cronoboardbackend.entity.TaskTag;
import com.cronoboard.cronoboardbackend.entity.TaskTagKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskTagRepository extends JpaRepository<TaskTag, TaskTagKey> {}
