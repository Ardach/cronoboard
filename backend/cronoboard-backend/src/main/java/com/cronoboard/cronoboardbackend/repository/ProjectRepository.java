package com.cronoboard.cronoboardbackend.repository;

import com.cronoboard.cronoboardbackend.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByUserIdOrderByUpdatedAtDesc(Long userId);
}


