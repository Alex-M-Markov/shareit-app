package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalElementsInputException extends RuntimeException {

    public IllegalElementsInputException(String message) {
        super(message);
    }
}