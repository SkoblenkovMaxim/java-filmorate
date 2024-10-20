package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.film.FilmDto;
import ru.yandex.practicum.filmorate.model.film.FilmMapper;
import ru.yandex.practicum.filmorate.model.genre.FilmGenre;
import ru.yandex.practicum.filmorate.model.like.Like;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.genre.Genre;
import ru.yandex.practicum.filmorate.model.rating.Rating;
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

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;
    private final GenreStorage genreStorage;
    private final RatingStorage ratingStorage;
    private final FilmMapper filmMapper;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("likeDbStorage") LikeStorage likeStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("genreDbStorage") GenreStorage genreStorage,
                       @Qualifier("ratingDbStorage") RatingStorage ratingStorage,
                       FilmMapper filmMapper
    ) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.ratingStorage = ratingStorage;
        this.filmMapper = filmMapper;
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
    }

    // удаление лайка
    public void removeLike(Long filmId, Long userId) {
        likeStorage.removeLike(filmId, userId);
    }

    // вывод 10 наиболее популярных фильмов по количеству лайков
    public List<FilmDto> getPopular(Integer count) {
        if (count < 1) {
            throw new ValidationException("Количество фильмов для вывода не должно быть меньше 1");
        }
        return likeStorage
                .getAllLikes()
                .stream()
                .map(like -> filmMapper.toFilmDto(filmStorage.getFilm(like.getFilmId())))
                .collect(Collectors.toList());
    }

    public FilmDto saveFilm(FilmDto filmDto) {
        Film film = filmMapper.toFilm(filmDto);

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза фильма не должна быть ранее 28 декабря 1895 год");
        }

        Film savedFilm = filmStorage.saveFilm(film);

        Rating mpa = ratingStorage.getRatingById(film.getMpa().getId());

        if (mpa == null) {
            throw new ValidationException("рейтинг для film id: " + film.getId() + " не найден");
        }

        if (film.getGenres() != null) {
            Set<FilmGenre> filmGenreSet = new HashSet<>();
            film.getGenres().forEach(genre -> {
                if (genre.getId() != null) {
                    Genre genreFromDb = genreStorage.getGenreById(genre.getId());
                    if (genreFromDb != null) {
                        filmGenreSet.add(FilmGenre.builder()
                                .filmId(savedFilm.getId())
                                .genreId(genre.getId())
                                .build());
                    } else {
                        throw new ValidationException("жанр для film id: " + film.getId() + " не найден");
                    }
                }
            });
            filmGenreSet.forEach(filmGenre ->
                    genreStorage.createFilmGenre(FilmGenre.builder()
                            .filmId(filmGenre.getFilmId())
                            .genreId(filmGenre.getGenreId())
                            .build()));
        }

        savedFilm.setGenres(film.getGenres());
        return filmMapper.toFilmDto(savedFilm);
    }

    public void removeFilm(Long filmId) {
        filmStorage.removeFilm(filmId);
    }

    public FilmDto updateFilm(FilmDto filmDto) {
        Film film = filmMapper.toFilm(filmDto);

        Film filmFromDb = filmStorage.getFilm(film.getId());
        if (filmFromDb != null) {
            return filmMapper.toFilmDto(filmStorage.updateFilm(film));
        } else {
            throw new NotFoundException("id " + film.getId() + " не найден");
        }
    }

    public FilmDto getFilm(Long filmId) {
        Film filmFromDb = filmStorage.getFilm(filmId);

        if (filmFromDb == null) {
            throw new NotFoundException("id " + filmId + " не найден");
        }

        Rating rating = ratingStorage.getRatingById(filmFromDb.getMpa().getId());
        filmFromDb.getMpa().setName(rating.getName());
        filmFromDb.getMpa().setDescription(rating.getDescription());

        List<Genre> genreList = new ArrayList<>();
        genreStorage.getFilmGenresByFilmId(filmFromDb.getId())
                .forEach(filmGenre -> {
                            Genre genre = genreStorage.getGenreById(filmGenre.getGenreId());
                            genreList.add(genre);
                        }
                );
        genreList.sort(Comparator.comparing(Genre::getId));
        filmFromDb.setGenres(genreList);

        return filmMapper.toFilmDto(filmFromDb);
    }

    public Collection<FilmDto> getFilms() {
        return filmStorage.getFilms().stream()
                .map(filmMapper::toFilmDto)
                .collect(Collectors.toList());
    }
}
