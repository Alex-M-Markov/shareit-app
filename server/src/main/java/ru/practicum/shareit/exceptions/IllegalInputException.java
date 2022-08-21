package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class IllegalInputException extends RuntimeException {

    public IllegalInputException(String message) {
        super(message);
    }
}
