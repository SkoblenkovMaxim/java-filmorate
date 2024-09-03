package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class FilmController {
    private final Map<Long, Film> allFilms = new HashMap<>();

    // Счетчик id фильмов
    public long countIdFilm() {
        long count = allFilms.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++count;
    }

    // Добавление фильма
    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film newFilm) {
        newFilm.setId(countIdFilm());
        if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза фильма не должна быть ранее 28 декабря 1895 год");
        }
        allFilms.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    // Обновление фильма
    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("Должен быть указан id фильма");
        }
        if (allFilms.containsKey(newFilm.getId())) {
            Film oldFilm = allFilms.get(newFilm.getId());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            return oldFilm;
        }
        throw new ValidationException("Фильм " + newFilm.getId() + " - " + newFilm.getName() + " не найден");
    }

    // Получение всех фильмов
    @GetMapping("/films")
    public Collection<Film> getFilms() {
        return allFilms.values();
    }
}
