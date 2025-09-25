package com.munivel.Employee_Management_System.Exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// @RestControllerAdvice is a Spring annotation that allows you to handle exceptions globally for
// all your REST controllers. Think of it as a centralized error handler.
//
// Here’s a breakdown:
//
// 1️⃣ Purpose
//
// In a Spring Boot REST API, when something goes wrong (validation fails, entity not found, etc.),
// exceptions are thrown.
//
// Without @RestControllerAdvice, each controller would need its own try-catch blocks.
@RestControllerAdvice
public class GlobalExceptionHandler {
  // GlobalExceptionHandler
  // Converts exception into readable JSON response
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }
}
// What you should remember
//
// Why we need it
//
// By default, Spring only gives "Bad Request" for validation errors.
//
// With @RestControllerAdvice + @ExceptionHandler, we can catch those errors globally and return a
// clear JSON response.
//
// How it works
//
// @RestControllerAdvice → Tells Spring this class handles exceptions for all controllers.
//
// @ExceptionHandler(MethodArgumentNotValidException.class) → Catches validation errors from @Valid.
//
// Inside, we extract field + message and send it as a custom response.
//
// When to use
//
// Whenever you use Bean Validation (@Valid, @NotBlank, @Email, etc.).
//
// To make error messages user-friendly instead of just HTTP 400.
//
// 🔹 Trick to remember
//
// Think:
//
// Advice = “Global advice for all controllers.”
//
// Handler = “Catch exceptions and respond with useful messages.”
