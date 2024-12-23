package ru.yandex.practicum.filmorate.controller.film;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.FilmDto;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
@Validated
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
        if (!("year".equals(sortBy) || "likes".equals(sortBy))) {
            throw new IllegalArgumentException("Invalid value for 'by' parameter. Allowed 'director' and 'title'.");
        }
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    //поиск GET /films/search?query=крад&by=director,title
    @GetMapping("/search")
    public List<FilmDto> searchFilms(
            @RequestParam @NotBlank(message = "query param cannot be blank") String query,
            @RequestParam @NotBlank(message = "by param cannot be blank") String by
    ) {
        if (!isSearchParamCorrect(by)) {
            throw new IllegalArgumentException("Invalid value for 'by' parameter. Allowed 'director' and 'title'.");
        }
        return filmService.searchFilms(query, by);
    }

    @GetMapping("/common")
    public List<FilmDto> getCommonFilms(
            @RequestParam("userId") @NotNull(message = "userId cannot be null") Long userId,
            @RequestParam("friendId") @NotNull(message = "friendId cannot be null") Long friendId
    ) {
        return filmService.getCommonFilms(userId, friendId);
    }

    private boolean isSearchParamCorrect(String by) {
        return "director".equals(by) || "title".equals(by) ||
                "title,director".equals(by) || "director,title".equals(by);
    }
}
