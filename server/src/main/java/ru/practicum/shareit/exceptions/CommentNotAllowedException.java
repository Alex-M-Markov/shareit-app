package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CommentNotAllowedException extends RuntimeException {

    public CommentNotAllowedException(String message) {
        super(message);
    }
}