package ru.yandex.practicum.filmorate.controller.genre;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.genre.Genre;
import ru.yandex.practicum.filmorate.service.genre.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genre")
public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<Genre> getGenres() {
        return genreService.getGenres();
    }

    @GetMapping("/{genreId}")
    public Genre getGenreById(@PathVariable Integer genreId) {
        return genreService.getGenreById(genreId);
    }

//    public List<Genre> getGenresFilm(@PathVariable Long filmId) {
//        return genreService.getGenresFilm(filmId);
//    }
}
