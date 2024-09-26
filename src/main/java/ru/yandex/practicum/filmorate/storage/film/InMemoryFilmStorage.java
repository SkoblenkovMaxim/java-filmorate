package ru.yandex.practicum.filmorate.storage.film;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.service.IdGenerator;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private IdGenerator idGenerator = new IdGenerator();
    private final Map<Long, Film> films = new HashMap<>();

    public InMemoryFilmStorage(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @PostMapping("/films")
    public Film saveFilm(@Valid @RequestBody Film newFilm) {
        newFilm.setId(idGenerator.getNextId());

        if (newFilm.getName() == null || newFilm.getName().isEmpty() || newFilm.getName().isBlank()) {
            log.error("Название фильма не может быть пустым");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза фильма не должна быть ранее 28 декабря 1895 год");
            throw new ValidationException("Дата релиза фильма не должна быть ранее 28 декабря 1895 год");
        }
        if (newFilm.getDescription() == null || newFilm.getDescription().isEmpty()
                || newFilm.getDescription().isBlank()) {
            log.error("Должно быть добавлено описание фильма");
            throw new ValidationException("Должно быть добавлено описание фильма");
        }
        if (newFilm.getDuration() <= 0) {
            log.error("Продолжительность фильма должна быть больше ноля");
            throw new ValidationException("Продолжительность фильма должна быть больше ноля");
        }

        films.put(newFilm.getId(), newFilm);
        log.debug("Фильм {} успешно сохранен", newFilm);
        return newFilm;
    }

    @DeleteMapping("/films")
    public Film removeFilm(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            films.remove(film.getId());
            log.debug("Фильм {} успешно удален", film.getName());
        }
        log.error("Фильм не найден");
        throw new ValidationException("Фильм не найден");
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (film.getId() == null) {
            log.error("Должен быть указан id фильма");
            throw new ValidationException("Должен быть указан id фильма");
        }
        if (films.containsKey(film.getId())) {
            Film oldFilm = films.get(film.getId());
            if (film.getName() != null) {
                oldFilm.setName(film.getName());
            }
            if (film.getReleaseDate() != null) {
                oldFilm.setReleaseDate(film.getReleaseDate());
            }
            if (film.getDescription() != null) {
                oldFilm.setDescription(film.getDescription());
            }
            if (film.getDuration() > 0) {
                oldFilm.setDuration(film.getDuration());
            }
            log.debug("Фильм успешно обновлён");
            return oldFilm;
        }
        throw new NotFoundException("Фильм " + film.getId() + " - " + film.getName() + " не найден");
    }

    @GetMapping("/films/{filmId}")
    public Film getFilm(@Valid @RequestBody Long filmId) {
        if (films.containsKey(filmId)) {
            return films.get(filmId);
        }
        log.debug("Фильм с id={} не найден", filmId);
        throw new NotFoundException("Фильм с id=" + filmId + " не найден");
    }

    @GetMapping("/films")
    public Collection<Film> getFilms() {
        return films.values();
    }
}