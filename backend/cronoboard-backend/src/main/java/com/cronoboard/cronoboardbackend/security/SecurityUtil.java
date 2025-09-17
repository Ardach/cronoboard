package com.cronoboard.cronoboardbackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class SecurityUtil {

    private final JwtService jwtService;

    public SecurityUtil(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public Long getCurrentUserId() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (!(attrs instanceof ServletRequestAttributes servletAttrs)) {
            throw new IllegalStateException("unauthorized");
        }
        HttpServletRequest request = servletAttrs.getRequest();
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new IllegalStateException("unauthorized");
        }
        String token = auth.substring(7);
        Jws<Claims> jws = jwtService.parse(token);
        Object uid = jws.getBody().get("uid");
        if (uid == null) throw new IllegalStateException("unauthorized");
        if (!(uid instanceof Number)) throw new IllegalStateException("unauthorized");
        return ((Number) uid).longValue();
    }
}


