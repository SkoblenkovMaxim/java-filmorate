package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.director.Director;
import java.util.Collection;
import java.util.List;

public interface DirectorStorage {

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(Long directorId);

    Director getDirectorById(Long directorId);

    Collection<Director> getAllDirectors();

    void addDirectorsToFilm(Long filmId, List<Long> directorIds);

    void removeDirectorsFromFilm(Long filmId);

    List<Director> getDirectorsByFilmId(Long filmId);

    List<Long> getFilmIdsByDirectorId(Long directorId);
}
