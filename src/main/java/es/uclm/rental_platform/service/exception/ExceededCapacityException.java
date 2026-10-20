package es.uclm.rental_platform.service.exception;

public class ExceededCapacityException extends RuntimeException {
    public ExceededCapacityException(String message) {
        super(message);
    }
}
