package io.acordi.classroom.presentation.handler;

import io.acordi.classroom.infrastructure.exception.TurmaNotFoundException;
import io.acordi.classroom.infrastructure.exception.TurmaValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(TurmaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTurmaNotFoundException(
            TurmaNotFoundException ex, HttpServletRequest request) {
        
        log.warn("Turma não encontrada: {}", ex.getMessage());
        
        return buildErrorResponse(
            HttpStatus.NOT_FOUND,
            "TURMA_NOT_FOUND",
            ex.getMessage(),
            request.getRequestURI()
        );
    }
    
    @ExceptionHandler(TurmaValidationException.class)
    public ResponseEntity<ErrorResponse> handleTurmaValidationException(
            TurmaValidationException ex, HttpServletRequest request) {
        
        log.warn("Erro de validação de turma: {}", ex.getMessage());
        
        return buildErrorResponse(
            HttpStatus.CONFLICT,
            "TURMA_VALIDATION_ERROR",
            ex.getMessage(),
            request.getRequestURI()
        );
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        
        log.warn("Argumento inválido: {}", ex.getMessage());
        
        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "INVALID_ARGUMENT",
            ex.getMessage(),
            request.getRequestURI()
        );
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        log.warn("Erro de validação nos dados de entrada");
        
        List<FieldError> fieldErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> new FieldError(
                error.getField(),
                error.getDefaultMessage(),
                Optional.ofNullable(error.getRejectedValue())
                    .map(Object::toString)
                    .orElse(null)
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.badRequest().body(
            new ValidationErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_FAILED",
                "Dados de entrada inválidos",
                request.getRequestURI(),
                fieldErrors
            )
        );
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        log.warn("Violação de restrições: {}", ex.getMessage());
        
        List<FieldError> fieldErrors = ex.getConstraintViolations()
            .stream()
            .map(this::mapConstraintViolation)
            .collect(Collectors.toList());
        
        return ResponseEntity.badRequest().body(
            new ValidationErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "CONSTRAINT_VIOLATION",
                "Violação de restrições de validação",
                request.getRequestURI(),
                fieldErrors
            )
        );
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        
        log.warn("Tipo de argumento inválido: {} para o parâmetro {}", 
            ex.getValue(), ex.getName());
        
        String message = String.format(
            "Valor '%s' inválido para o parâmetro '%s'. Esperado tipo: %s",
            ex.getValue(),
            ex.getName(),
            Optional.ofNullable(ex.getRequiredType())
                .map(Class::getSimpleName)
                .orElse("desconhecido")
        );
        
        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "INVALID_PARAMETER_TYPE",
            message,
            request.getRequestURI()
        );
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        
        log.warn("Erro ao ler mensagem HTTP: {}", ex.getMessage());
        
        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "MALFORMED_JSON",
            "Formato JSON inválido ou dados mal formados",
            request.getRequestURI()
        );
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        log.error("Erro interno do servidor", ex);
        
        return buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_SERVER_ERROR",
            "Erro interno do servidor. Tente novamente mais tarde.",
            request.getRequestURI()
        );
    }
    
    private ResponseEntity<ErrorResponse> buildErrorResponse(
            HttpStatus status, String errorCode, String message, String path) {
        
        return ResponseEntity.status(status).body(
            new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                errorCode,
                message,
                path
            )
        );
    }
    
    private FieldError mapConstraintViolation(ConstraintViolation<?> violation) {
        String fieldName = violation.getPropertyPath().toString();
        return new FieldError(
            fieldName,
            violation.getMessage(),
            Optional.ofNullable(violation.getInvalidValue())
                .map(Object::toString)
                .orElse(null)
        );
    }
    
    public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
    ) {}
    
    public record ValidationErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldError> fieldErrors
    ) {}
    
    public record FieldError(
        String field,
        String message,
        String rejectedValue
    ) {}
}