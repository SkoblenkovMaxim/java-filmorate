package ru.yandex.practicum.filmorate.storage.genre;

import java.util.List;

import ru.yandex.practicum.filmorate.model.genre.FilmGenre;
import ru.yandex.practicum.filmorate.model.genre.Genre;

public interface GenreStorage {

    void createFilmGenre(FilmGenre filmGenre);

    Genre getGenreById(Long genreId);

    List<FilmGenre> getFilmGenresByFilmId(Long filmId);

    List<Genre> getGenres();

    //boolean isContains(Long id);
}
