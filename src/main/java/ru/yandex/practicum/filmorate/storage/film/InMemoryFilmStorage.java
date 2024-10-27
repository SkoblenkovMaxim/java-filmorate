package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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

    private final IdGenerator idGenerator = new IdGenerator();

    private final Map<Long, Film> films = new HashMap<>();

    public Film saveFilm(Film newFilm) {
        newFilm.setId(idGenerator.getNextId());

        if (newFilm.getName() == null || newFilm.getName().isEmpty()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза фильма не должна быть ранее 28 декабря 1895 год");
        }

        if (newFilm.getDescription() == null || newFilm.getDescription().isEmpty()) {
            throw new ValidationException("Должно быть добавлено описание фильма");
        }

        if (newFilm.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть больше ноля");
        }

        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    public void removeFilm(Long filmId) {
        if (isValidFilm(filmId)) {
            films.remove(filmId);
        }
        throw new ValidationException("Фильм не найден");
    }

    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            throw new ValidationException("Должен быть указан id фильма");
        }
        if (isValidFilm(film.getId())) {
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
            return oldFilm;
        }
        throw new NotFoundException("Фильм " + film.getId() + " - " + film.getName() + " не найден");
    }

    public Film getFilm(Long filmId) {
        if (isValidFilm(filmId)) {
            return films.get(filmId);
        }
        throw new NotFoundException("Фильм с id=" + filmId + " не найден");
    }

    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public List<Long> getUsersRecommendations(Long userId) {
        return List.of();
    }

    @Override
    public List<Long> getFilmsLikesByUser(Long userId) {
        return List.of();
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return List.of();
    }

    @Override
    public List<Film> getFilmsByDirector(Long directorId) {
        return List.of();
    }

    @Override
    public List<Film> getSearch(String query, String by) {
        return List.of();
    }

    public boolean isValidFilm(Long filmId) {
        return films.containsKey(filmId);
    }
}
