package com.foodie.order;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.*;
import org.springframework.web.server.ResponseStatusException;
@Component
class CustomerAuthorization {
    private final RestClient users;
    CustomerAuthorization(RestClient.Builder builder) { users=builder.baseUrl("http://user-service").build(); }
    record Identity(Long customerId, String role) {}
    Long authenticate(String token) {
        if(token==null || !token.startsWith("Bearer ")) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Bearer token required");
        Identity identity;
        try { identity=users.get().uri("/api/auth/me").header("Authorization",token).retrieve().body(Identity.class); }
        catch(HttpClientErrorException.Unauthorized e) { throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid or expired token"); }
        catch(RestClientException e) { throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Authentication unavailable"); }
        if(identity==null || identity.customerId()==null) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Authentication unavailable");
        if(!"CUSTOMER".equals(identity.role())) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Customer role required");
        return identity.customerId();
    }
    void requireOwner(String token, Long id) {
        if(!authenticate(token).equals(id)) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Cannot access another customer's data");
    }
}
