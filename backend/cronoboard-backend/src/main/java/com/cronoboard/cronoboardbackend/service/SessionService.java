package com.cronoboard.cronoboardbackend.service;

import com.cronoboard.cronoboardbackend.dto.session.SessionCreateRequest;
import com.cronoboard.cronoboardbackend.entity.*;
import com.cronoboard.cronoboardbackend.repository.PomodoroSessionRepository;
import com.cronoboard.cronoboardbackend.repository.SessionEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SessionService {

    private final PomodoroSessionRepository sessionRepo;
    private final SessionEventRepository eventRepo;
    private final TaskService taskService;

    public SessionService(PomodoroSessionRepository sessionRepo, SessionEventRepository eventRepo, TaskService taskService) {
        this.sessionRepo = sessionRepo;
        this.eventRepo = eventRepo;
        this.taskService = taskService;
    }

    // --- Helpers ---

    private void appendEvent(Long sessionId, SessionEventType type) {
        var ev = new SessionEvent();
        ev.setSessionId(sessionId);
        ev.setEventType(type);
        ev.setAt(OffsetDateTime.now());
        eventRepo.save(ev);
    }


    /**
     * Şu ana kadarki "running" odak süresini hesaplar.
     * Basit kural: lastAnchor = son {start,resume};
     * running'i kesen son olay {pause,finish,abort}.
     * Bu aralıktaki süre focusSeconds'a eklenir.
     */
    private int computeAdditionalFocusSeconds(Long sessionId) {
        List<SessionEvent> events = eventRepo.findBySessionIdOrderByAtAsc(sessionId);
        OffsetDateTime anchor = null;
        int acc = 0;

        for (SessionEvent e : events) {
            switch (e.getEventType()) {
                case start, resume -> {
                    // running başlar
                    anchor = e.getAt();
                }
                case pause, finish, abort -> {
                    // running kesilir
                    if (anchor != null) {
                        acc += Math.max(0, (int) Duration.between(anchor, e.getAt()).getSeconds());
                        anchor = null;
                    }
                }
                default -> {}
            }
        }
        // Eğer halen running ise, şimdiye kadar olan kısmı ekle
        if (anchor != null) {
            acc += Math.max(0, (int) Duration.between(anchor, OffsetDateTime.now()).getSeconds());
        }
        return acc;
    }

    // --- Commands ---

    @Transactional
    public PomodoroSession start(SessionCreateRequest req) {
        var s = new PomodoroSession();
        s.setUserId(req.getUserId());
        s.setPlannedMinutes(Optional.ofNullable(req.getPlannedMinutes()).orElse(25));
        s.setStartedAt(OffsetDateTime.now());
        s.setState(SessionState.running);

        if (req.getTaskId() != null) {
            var task = taskService.requireStartable(req.getTaskId(), req.getUserId());
            s.setTaskId(task.getId());
            taskService.markInProgressIfTodo(task); // sadece to do → in_progress
        }

        s = sessionRepo.save(s);
        appendEvent(s.getId(), SessionEventType.start);
        return s;
    }

    @Transactional
    public PomodoroSession pause(Long id, Long userId) {
        var s = requireRunningOwned(id, userId);
        appendEvent(s.getId(), SessionEventType.pause);
        // pause ile running kesildi → o ana kadar olanı ekle
        s.setFocusSeconds(computeAdditionalFocusSeconds(s.getId()));
        return sessionRepo.save(s);
    }

    @Transactional
    public PomodoroSession resume(Long id, Long userId) {
        var s = requireRunningOwned(id, userId);
        // en son pause olması beklenir; basit kontrolde sadece state yeter (running)
        appendEvent(s.getId(), SessionEventType.resume);
        return s;
    }


    @Transactional
    public PomodoroSession finish(Long id, Long userId) {
        var s = requireRunningOwned(id, userId);
        appendEvent(s.getId(), SessionEventType.finish);
        s.setFocusSeconds(computeAdditionalFocusSeconds(s.getId()));
        s.setEndedAt(OffsetDateTime.now());
        s.setState(SessionState.completed);
        if (s.getTaskId() != null) {
            taskService.applyFocusProgress(s.getTaskId(), userId, s.getFocusSeconds(), true);
        }
        return sessionRepo.save(s);
    }

    @Transactional
    public PomodoroSession abort(Long id, Long userId) {
        var s = requireRunningOwned(id, userId);
        appendEvent(s.getId(), SessionEventType.abort);
        s.setFocusSeconds(computeAdditionalFocusSeconds(s.getId()));
        s.setEndedAt(OffsetDateTime.now());
        s.setState(SessionState.aborted);
        // Abort = tam iptal: çalışılan süre task ilerlemesine YAZILMAZ
        return sessionRepo.save(s);
    }

    // --- Queries ---

    @Transactional(readOnly = true)
    public PomodoroSession get(Long id) {
        return sessionRepo.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<PomodoroSession> list(Long userId, OffsetDateTime from, OffsetDateTime to) {
        if (from != null && to != null) {
            return sessionRepo.findByUserIdAndStartedAtBetweenOrderByStartedAtDesc(userId, from, to);
        }
        return sessionRepo.findByUserIdOrderByStartedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<SessionEvent> events(Long sessionId) {
        return eventRepo.findBySessionIdOrderByAtAsc(sessionId);
    }

    // --- Guards ---

    private PomodoroSession requireRunningOwned(Long id, Long userId) {
        var s = sessionRepo.findById(id).orElseThrow(() -> new IllegalStateException("session not found"));
        if (!s.getUserId().equals(userId)) throw new IllegalStateException("forbidden");
        if (s.getState() != SessionState.running) throw new IllegalStateException("invalid state");
        return s;
    }
}
