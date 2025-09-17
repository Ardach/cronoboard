package com.cronoboard.cronoboardbackend.repository;

import com.cronoboard.cronoboardbackend.entity.PomodoroSession;
import com.cronoboard.cronoboardbackend.entity.SessionState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface PomodoroSessionRepository extends JpaRepository<PomodoroSession, Long> {
    List<PomodoroSession> findByUserIdAndStartedAtBetweenOrderByStartedAtDesc(
        Long userId, OffsetDateTime from, OffsetDateTime to);
    List<PomodoroSession> findByUserIdOrderByStartedAtDesc(Long userId);
    boolean existsByIdAndUserIdAndState(Long id, Long userId, SessionState state);
}
