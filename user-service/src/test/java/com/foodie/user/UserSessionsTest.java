package com.foodie.user;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserSessionsTest {
    @Test void validatesServerIssuedTokensAndRoles() {
        var repository = mock(CustomerRepository.class);
        var sessions = new UserSessions(repository);
        var user = new Customer(); user.customerId=1L; user.role=UserRole.ADMIN;
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        String token=sessions.issue(user);
        assertDoesNotThrow(() -> sessions.requireAdmin("Bearer " + token));
        assertEquals(401, assertThrows(ResponseStatusException.class, () -> sessions.requireAdmin("Bearer forged")).getStatusCode().value());
        assertEquals(401, assertThrows(ResponseStatusException.class, () -> sessions.requireAdmin(null)).getStatusCode().value());
        user.role=UserRole.CUSTOMER;
        assertEquals(403, assertThrows(ResponseStatusException.class, () -> sessions.requireAdmin("Bearer " + token)).getStatusCode().value());
    }
}
