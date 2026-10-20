package es.uclm.rental_platform.service.exception;

public class InvalidCredentialsException extends IllegalArgumentException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
