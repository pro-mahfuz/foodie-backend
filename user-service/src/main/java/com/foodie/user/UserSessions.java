package com.foodie.user;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
class UserSessions {
    private record Session(Long userId, Instant expiresAt) {}
    private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();
    private final CustomerRepository users;

    UserSessions(CustomerRepository users) { this.users = users; }

    String issue(Customer user) {
        sessions.entrySet().removeIf(e -> !e.getValue().expiresAt().isAfter(Instant.now()));
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        sessions.put(token, new Session(user.customerId, Instant.now().plusSeconds(28800)));
        return token;
    }

    Customer authenticate(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) throw unauthorized();
        Session session = sessions.get(authorization.substring(7));
        if (session == null || !session.expiresAt().isAfter(Instant.now())) throw unauthorized();
        return users.findById(session.userId()).orElseThrow(this::unauthorized);
    }

    void requireAdmin(String authorization) {
        if (authenticate(authorization).role != UserRole.ADMIN)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin role required");
    }

    private ResponseStatusException unauthorized() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Valid bearer token required; log in again");
    }
}
