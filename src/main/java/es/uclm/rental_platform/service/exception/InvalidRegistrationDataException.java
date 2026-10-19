package es.uclm.rental_platform.service.exception;

public class InvalidRegistrationDataException extends IllegalArgumentException {
    public InvalidRegistrationDataException(String message) {
        super(message);
    }
}
