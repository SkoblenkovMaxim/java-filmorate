package ru.yandex.practicum.filmorate.service.genre;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.genre.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Service
public class GenreService {

    private final GenreStorage genreStorage;
    //private final FilmStorage filmStorage;

    public GenreService(GenreStorage genreStorage, @Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.genreStorage = genreStorage;
        //this.filmStorage = filmStorage;
    }

    public List<Genre> getGenres() {
        return genreStorage.getGenres();
    }

    public Genre getGenreById(Long genreId) {
        if (genreId == null) {
            throw new NotFoundException("id genre не найден");
        }
        Genre genre = genreStorage.getGenreById(genreId);
        if (genre == null) throw new NotFoundException("id " + genreId + " не найден");
        return genre;
    }

}
