package ru.yandex.practicum.filmorate.controller.genre;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.genre.Genre;
import ru.yandex.practicum.filmorate.service.genre.GenreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public List<Genre> getGenres() {
        return genreService.getGenres();
    }

    @GetMapping("/{genreId}")
    public Genre getGenreById(@Valid @PathVariable Long genreId) {
        return genreService.getGenreById(genreId);
    }
}
