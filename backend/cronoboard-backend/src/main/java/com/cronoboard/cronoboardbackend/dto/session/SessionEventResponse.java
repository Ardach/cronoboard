package com.cronoboard.cronoboardbackend.dto.session;

import com.cronoboard.cronoboardbackend.entity.SessionEventType;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.OffsetDateTime;

public class SessionEventResponse {
    private Long id;
    private Long sessionId;
    private SessionEventType eventType;
    private OffsetDateTime at;
    private JsonNode meta; // ← String yerine JsonNode

    public SessionEventResponse(Long id, Long sessionId, SessionEventType eventType,
                                OffsetDateTime at, JsonNode meta) {
        this.id = id;
        this.sessionId = sessionId;
        this.eventType = eventType;
        this.at = at;
        this.meta = meta;
    }

    public Long getId() {
        return id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public SessionEventType getEventType() {
        return eventType;
    }

    public OffsetDateTime getAt() {
        return at;
    }

    public JsonNode getMeta() {
        return meta;
    }
}
