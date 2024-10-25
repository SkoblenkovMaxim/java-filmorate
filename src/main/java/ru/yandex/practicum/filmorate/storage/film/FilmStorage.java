package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Film saveFilm(Film film); // добавление фильма

    void removeFilm(Long filmId); // удаление фильма

    Film updateFilm(Film film); //модификация, обновление фильма

    Film getFilm(Long filmId);

    Collection<Film> getFilms();

    List<Film> getFilmsByDirector(Long directorId);

    List<Long> getUsersRecommendations(Long userId);

    List<Long> getFilmsLikesByUser(Long userId);


    List<Film> getSearch(String query, String by);
}
