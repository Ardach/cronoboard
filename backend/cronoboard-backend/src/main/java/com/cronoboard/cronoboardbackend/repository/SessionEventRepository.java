package com.cronoboard.cronoboardbackend.repository;

import com.cronoboard.cronoboardbackend.entity.SessionEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionEventRepository extends JpaRepository<SessionEvent, Long> {
    List<SessionEvent> findBySessionIdOrderByAtAsc(Long sessionId);

    SessionEvent findTopBySessionIdOrderByAtDesc(Long sessionId);
}
