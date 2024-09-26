package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;

@Service
public class IdGenerator {
    private Long id = 1L;

    public Long getNextId() {
        return id++;
    }
}
