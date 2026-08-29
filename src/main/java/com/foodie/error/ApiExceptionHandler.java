package com.foodie.error;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.*;

@RestControllerAdvice
public class ApiExceptionHandler {
 public record ErrorResponse(Instant timestamp,int status,String error,String message,Map<String,String> fieldErrors){}
 @ExceptionHandler(NotFoundException.class) ResponseEntity<ErrorResponse> notFound(NotFoundException e){return build(HttpStatus.NOT_FOUND,e.getMessage(),Map.of());}
 @ExceptionHandler(ConflictException.class) ResponseEntity<ErrorResponse> conflict(ConflictException e){return build(HttpStatus.CONFLICT,e.getMessage(),Map.of());}
 @ExceptionHandler(BadRequestException.class) ResponseEntity<ErrorResponse> bad(BadRequestException e){return build(HttpStatus.BAD_REQUEST,e.getMessage(),Map.of());}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException e){Map<String,String> fields=new LinkedHashMap<>();e.getBindingResult().getFieldErrors().forEach(x->fields.putIfAbsent(x.getField(),x.getDefaultMessage()));return build(HttpStatus.BAD_REQUEST,"Validation failed",fields);}
 private ResponseEntity<ErrorResponse> build(HttpStatus s,String m,Map<String,String> f){return ResponseEntity.status(s).body(new ErrorResponse(Instant.now(),s.value(),s.getReasonPhrase(),m,f));}
}
