package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.director.Director;
import ru.yandex.practicum.filmorate.model.event.EventOperation;
import ru.yandex.practicum.filmorate.model.film.FilmDto;
import ru.yandex.practicum.filmorate.model.film.FilmMapper;
import ru.yandex.practicum.filmorate.model.genre.FilmGenre;
import ru.yandex.practicum.filmorate.model.like.Like;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.genre.Genre;
import ru.yandex.practicum.filmorate.model.rating.Rating;
import ru.yandex.practicum.filmorate.service.event.EventService;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.raiting.RatingStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;
    private final GenreStorage genreStorage;
    private final RatingStorage ratingStorage;
    private final DirectorStorage directorStorage;
    private final FilmMapper filmMapper;
    private final EventService eventService;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("likeDbStorage") LikeStorage likeStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("genreDbStorage") GenreStorage genreStorage,
                       @Qualifier("ratingDbStorage") RatingStorage ratingStorage,
                       @Qualifier("directorDbStorage") DirectorStorage directorStorage,
                       FilmMapper filmMapper,
                       EventService eventService) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.ratingStorage = ratingStorage;
        this.directorStorage = directorStorage;
        this.filmMapper = filmMapper;
        this.eventService = eventService;
    }

    // добавление лайка
    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilm(filmId);
        if (film != null) {
            if (userStorage.getUserById(userId) != null) {
                likeStorage.createLike(Like.builder()
                        .filmId(filmId)
                        .userId(userId)
                        .build()
                );
            } else {
                throw new NotFoundException("Пользователь c ID=" + userId + " не найден!");
            }
        } else {
            throw new NotFoundException("Фильм c ID=" + filmId + " не найден!");
        }
        eventService.createLikeEvent(filmId, userId, EventOperation.ADD);
    }

    // удаление лайка
    public void removeLike(Long filmId, Long userId) {
        eventService.createLikeEvent(filmId, userId, EventOperation.REMOVE);
        likeStorage.removeLike(filmId, userId);
    }

    public List<FilmDto> getPopularFilms(Integer limit, Integer genreId, Integer year) {
        if (limit < 1) {
            throw new ValidationException("Количество фильмов для вывода должно быть больше 0");
        }

        List<Film> films = filmStorage.getPopularFilms(limit, genreId, year);
        films.forEach(this::fillFilmAdditionalInfo);

        return films.stream()
                .map(filmMapper::toFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto saveFilm(FilmDto filmDto) {
        Film film = filmMapper.toFilm(filmDto);

        if (film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            throw new ValidationException(
                    "Дата релиза фильма не должна быть ранее 28 декабря 1895 год");
        }

        Film savedFilm = filmStorage.saveFilm(film);
        Rating mpa = ratingStorage.getRatingById(film.getMpa().getId());

        if (mpa == null) {
            throw new ValidationException("рейтинг для film id: " + film.getId() + " не найден");
        }

        if (film.getGenres() != null) {
            Set<FilmGenre> filmGenres = film.getGenres().stream()
                    .filter(genre -> genre.getId() != null)
                    .map(genre -> {
                        Genre genreFromDb = genreStorage.getGenreById(genre.getId());
                        if (genreFromDb == null) {
                            throw new ValidationException(
                                    "жанр для film id: " + film.getId() + " не найден");
                        }
                        return FilmGenre.builder().filmId(savedFilm.getId()).genreId(genre.getId())
                                .build();
                    }).collect(Collectors.toSet());
            filmGenres.forEach(genreStorage::createFilmGenre);
        }

        if (film.getDirectors() != null) {
            List<Long> directorIds = film.getDirectors().stream()
                    .filter(director -> director.getId() != null)
                    .map(director -> {
                        Director directorFromDb = directorStorage.getDirectorById(director.getId());
                        if (directorFromDb == null) {
                            throw new ValidationException(
                                    "режиссёр для film id: " + film.getId() + " не найден");
                        }
                        return director.getId();
                    }).collect(Collectors.toList());
            directorStorage.addDirectorsToFilm(savedFilm.getId(), directorIds);
        }

        return getFilm(savedFilm.getId());
    }

    public void removeFilm(Long filmId) {
        directorStorage.removeDirectorsFromFilm(filmId);
        filmStorage.removeFilm(filmId);
    }

    public FilmDto updateFilm(FilmDto filmDto) {
        Film film = filmMapper.toFilm(filmDto);
        System.out.println(film);

        Film filmFromDb = filmStorage.getFilm(film.getId());
        if (filmFromDb == null) {
            throw new NotFoundException("id " + film.getId() + " не найден");
        }

        filmStorage.updateFilm(film);
        directorStorage.removeDirectorsFromFilm(film.getId());

        if (film.getDirectors() != null) {
            List<Long> directorIds = film.getDirectors().stream()
                    .filter(director -> director.getId() != null)
                    .map(director -> {
                        Director directorFromDb = directorStorage.getDirectorById(director.getId());
                        if (directorFromDb == null) {
                            throw new ValidationException(
                                    "режиссёр с id: " + director.getId() + " не найден");
                        }
                        return director.getId();
                    }).collect(Collectors.toList());
            directorStorage.addDirectorsToFilm(film.getId(), directorIds);
        }

        return getFilm(filmFromDb.getId());
    }

    public FilmDto getFilm(Long filmId) {
        Film filmFromDb = filmStorage.getFilm(filmId);

        if (filmFromDb == null) {
            throw new NotFoundException("id " + filmId + " не найден");
        }

        fillFilmAdditionalInfo(filmFromDb);

        return filmMapper.toFilmDto(filmFromDb);
    }

    public Collection<FilmDto> getFilms() {
        Collection<Film> films = filmStorage.getFilms();

        films.forEach(this::fillFilmAdditionalInfo);

        return films.stream()
                .map(filmMapper::toFilmDto)
                .collect(Collectors.toList());
    }

    public List<FilmDto> getFilmsByDirector(Long directorId, String sortBy) {
        if (!"year".equals(sortBy) && !"likes".equals(sortBy)) {
            throw new ValidationException("параметр sortBy должен быть 'year' или 'likes'");
        }

        Director director = directorStorage.getDirectorById(directorId);
        if (director == null) {
            throw new NotFoundException("режиссёр с id: " + directorId + " не найден");
        }

        List<Film> films = filmStorage.getFilmsByDirector(directorId);

        films.forEach(this::fillFilmAdditionalInfo);

        if ("year".equals(sortBy)) {
            films.sort(Comparator.comparing(Film::getReleaseDate));
        } else {
            films.sort((f1, f2) -> {
                int likes1 = likeStorage.getLikesCount(f1.getId());
                int likes2 = likeStorage.getLikesCount(f2.getId());
                return Integer.compare(likes2, likes1);
            });
        }

        return films.stream()
                .map(filmMapper::toFilmDto)
                .collect(Collectors.toList());
    }

    public List<FilmDto> getSearch(String query, String by) {
        List<Film> films = filmStorage.getSearch(query, by);

        films.forEach(this::fillFilmAdditionalInfo);
        return films.stream()
                .map(filmMapper::toFilmDto)
                .collect(Collectors.toList());
    }

    public List<FilmDto> getCommonFilms(Long userId, Long friendId) {
        List<Film> commonFilms = filmStorage.getCommonFilms(userId, friendId);
        commonFilms.forEach(this::fillFilmAdditionalInfo);

        return commonFilms.stream()
                .map(filmMapper::toFilmDto)
                .collect(Collectors.toList());
    }

    private void fillFilmAdditionalInfo(Film film) {
        Rating rating = ratingStorage.getRatingById(film.getMpa().getId());
        if (rating != null) {
            film.getMpa().setName(rating.getName());
            film.getMpa().setDescription(rating.getDescription());
        }

        List<Genre> genreList = new ArrayList<>();
        genreStorage.getFilmGenresByFilmId(film.getId())
                .forEach(filmGenre -> {
                    Genre genre = genreStorage.getGenreById(filmGenre.getGenreId());
                    if (genre != null) {
                        genreList.add(genre);
                    }
                });
        genreList.sort(Comparator.comparing(Genre::getId));
        film.setGenres(genreList);

        List<Director> directors = directorStorage.getDirectorsByFilmId(film.getId());
        film.setDirectors(directors);

        film.setLikeCount(likeStorage.getLikesCount(film.getId()));
    }
}
