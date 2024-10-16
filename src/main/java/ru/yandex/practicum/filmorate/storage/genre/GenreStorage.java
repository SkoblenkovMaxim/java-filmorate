package ru.yandex.practicum.filmorate.storage.genre;

import java.util.List;

import ru.yandex.practicum.filmorate.model.genre.Genre;

public interface GenreStorage {

    void createGenre(Genre genre);

    Genre getGenreById(Integer genreId); // добавление фильма

    List<Genre> getGenres();

    boolean isContains(Integer id);
}
