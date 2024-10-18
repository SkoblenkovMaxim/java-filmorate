package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InternalServerException extends RuntimeException {
    public InternalServerException(final String message) {
        super(message);
//        log.error(message);
    }
}
