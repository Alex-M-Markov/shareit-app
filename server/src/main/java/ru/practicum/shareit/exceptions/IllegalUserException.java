package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)

public class IllegalUserException extends RuntimeException {

    public IllegalUserException() {
        super("Эта вещь Вам не принадлежит");
    }
}
