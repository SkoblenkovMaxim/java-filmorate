package ru.yandex.practicum.filmorate.controller.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private  final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    // Добавление фильма
    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        filmService.saveFilm(film);
        return film;
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

    @GetMapping("/popular?count={count}")
    public List<Film> getTopFilms(
            @RequestParam(required = false, defaultValue = "10")
            @PathVariable Integer count) {
        return filmService.getTopFilms(count);
    }

    @DeleteMapping
    public Film removeFilm(Film film) {
        return filmService.removeFilm(film);
    }

    @GetMapping("/{filmId}")
    public Film getFilm(@PathVariable Long filmId) {
        return filmService.getFilm(filmId);
    }
}
