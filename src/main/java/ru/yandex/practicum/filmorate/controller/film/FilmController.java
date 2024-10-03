package ru.yandex.practicum.filmorate.controller.film;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    // Добавление фильма
    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.saveFilm(film);
    }

    // Обновление фильма
    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        return filmService.updateFilm(newFilm);
    }

    // Получение всех фильмов
    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getFilms();
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.removeLike(filmId, userId);
    }

    //@GetMapping("/popular?count={count}")
    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam("count") Integer count) {
        return filmService.getPopular(count);
    }

    @DeleteMapping("/{filmId}")
    public void removeFilm(@PathVariable Long filmId) {
        filmService.removeFilm(filmId);
    }

    @GetMapping("/{filmId}")
    public Film getFilm(@PathVariable Long filmId) {
        return filmService.getFilm(filmId);
    }
}
