package ru.yandex.practicum.filmorate.controller.film;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.FilmDto;
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
    public FilmDto addFilm(@Valid @RequestBody FilmDto filmDto) {
        return filmService.saveFilm(filmDto);
    }

    // Обновление фильма
    @PutMapping
    public FilmDto updateFilm(@Valid @RequestBody FilmDto newFilm) {
        return filmService.updateFilm(newFilm);
    }

    // Получение всех фильмов
    @GetMapping
    public Collection<FilmDto> getFilms() {
        return filmService.getFilms();
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@Valid @PathVariable Long filmId, @Valid @PathVariable Long userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@Valid @PathVariable Long filmId, @Valid @PathVariable Long userId) {
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<FilmDto> getPopularFilms(
            @RequestParam(value = "count", defaultValue = "10") Integer count,
            @RequestParam(value = "genreId", required = false) Integer genreId,
            @RequestParam(value = "year", required = false) Integer year
    ) {
        return filmService.getPopularFilms(count, genreId, year);
    }

    @DeleteMapping("/{filmId}")
    public void removeFilm(@Valid @PathVariable Long filmId) {
        filmService.removeFilm(filmId);
    }

    @GetMapping("/{filmId}")
    public FilmDto getFilm(@Valid @PathVariable Long filmId) {
        return filmService.getFilm(filmId);
    }

    @GetMapping("/director/{directorId}")
    public List<FilmDto> getFilmsByDirector(@PathVariable Long directorId,
            @RequestParam("sortBy") String sortBy) {
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    //поиск GET /films/search?query=крад&by=director,title
    @GetMapping("/search")
    public List<FilmDto> getSearch(@RequestParam String query, @RequestParam String by) {
        return filmService.getSearch(query, by);
    }
}
