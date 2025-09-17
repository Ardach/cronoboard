package com.cronoboard.cronoboardbackend.controller;

import com.cronoboard.cronoboardbackend.dto.session.*;
import com.cronoboard.cronoboardbackend.entity.SessionEvent;
import com.cronoboard.cronoboardbackend.entity.PomodoroSession;
import com.cronoboard.cronoboardbackend.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService service;

    public SessionController(SessionService service) {
        this.service = service;
    }

    // --- Create ---
    @PostMapping
    public ResponseEntity<SessionResponse> start(@Valid @RequestBody SessionCreateRequest req) {
        PomodoroSession s = service.start(req);
        return ResponseEntity.status(201).body(toResponse(s));
    }

    // --- Queries ---
    @GetMapping("/{id}")
    public ResponseEntity<SessionResponse> get(@PathVariable Long id) {
        var s = service.get(id);
        return (s == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(toResponse(s));
    }

    @GetMapping
    public List<SessionResponse> list(@RequestParam Long userId,
                                      @RequestParam(required = false)
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
                                      @RequestParam(required = false)
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to) {
        return service.list(userId, from, to).stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}/events")
    public List<SessionEventResponse> events(@PathVariable Long id) {
        List<SessionEvent> evs = service.events(id);
        return evs.stream()
            .map(e -> new SessionEventResponse(e.getId(), e.getSessionId(), e.getEventType(), e.getAt(), e.getMeta()))
            .toList();
    }

    // --- Actions (userId query paramı ile basit auth) ---
    @PostMapping("/{id}/pause")
    public ResponseEntity<?> pause(@PathVariable Long id, @RequestParam Long userId) {
        return wrap(() -> toResponse(service.pause(id, userId)));
    }

    @PostMapping("/{id}/resume")
    public ResponseEntity<?> resume(@PathVariable Long id, @RequestParam Long userId) {
        return wrap(() -> toResponse(service.resume(id, userId)));
    }


    @PostMapping("/{id}/finish")
    public ResponseEntity<?> finish(@PathVariable Long id, @RequestParam Long userId) {
        return wrap(() -> toResponse(service.finish(id, userId)));
    }

    @PostMapping("/{id}/abort")
    public ResponseEntity<?> abort(@PathVariable Long id, @RequestParam Long userId) {
        return wrap(() -> toResponse(service.abort(id, userId)));
    }

    // --- Helpers ---
    private SessionResponse toResponse(PomodoroSession s) {
        return new SessionResponse(
            s.getId(), s.getUserId(), s.getTaskId(), s.getPlannedMinutes(), s.getFocusSeconds(),
            s.getStartedAt(), s.getEndedAt(), s.getState()
        );
    }

    private ResponseEntity<?> wrap(SupplierWithException<SessionResponse> supplier) {
        try {
            return ResponseEntity.ok(supplier.get());
        } catch (IllegalStateException ex) {
            String msg = ex.getMessage();
            if ("session not found".equals(msg)) return ResponseEntity.notFound().build();
            if ("task not found".equals(msg)) return ResponseEntity.status(404).body(msg);
            if ("forbidden".equals(msg)) return ResponseEntity.status(403).body(msg);
            if ("invalid state".equals(msg)) return ResponseEntity.status(409).body(msg);
            if ("task not startable".equals(msg)) return ResponseEntity.status(409).body(msg);
            return ResponseEntity.badRequest().body(msg);
        }
    }

    @FunctionalInterface
    interface SupplierWithException<T> {
        T get() throws IllegalStateException;
    }
}
