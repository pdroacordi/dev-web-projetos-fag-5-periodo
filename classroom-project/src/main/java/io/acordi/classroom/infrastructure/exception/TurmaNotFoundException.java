package io.acordi.classroom.infrastructure.exception;

public class TurmaNotFoundException extends RuntimeException {
    
    public TurmaNotFoundException(String message) {
        super(message);
    }
}