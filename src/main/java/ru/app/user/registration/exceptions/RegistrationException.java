package ru.app.user.registration.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class RegistrationException extends RuntimeException {

    private final String message;
    private final HttpStatus httpStatus;

}
