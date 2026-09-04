package com.foodie.food;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
class AdminAuthorization {
    private final DiscoveryClient discovery;
    private final ObjectMapper mapper;
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
    AdminAuthorization(DiscoveryClient discovery, ObjectMapper mapper) { this.discovery = discovery; this.mapper = mapper; }

    void requireAdmin(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer "))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bearer token required");
        var instances = discovery.getInstances("user-service");
        if (instances.isEmpty()) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Authentication unavailable");
        try {
            var request = HttpRequest.newBuilder(URI.create(instances.get(0).getUri() + "/api/auth/me"))
                .timeout(Duration.ofSeconds(5)).header("Authorization", authorization).GET().build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 401) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
            if (response.statusCode() != 200) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Authentication unavailable");
            if (!"ADMIN".equals(mapper.readTree(response.body()).path("role").asText()))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin role required");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Authentication unavailable");
        } catch (java.io.IOException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Authentication unavailable");
        }
    }
}
