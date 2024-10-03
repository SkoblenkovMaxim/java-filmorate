package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class LikeStorage {

    // Хранилище ид пользователя и списка фильмов, которые он лайкнул
    private final Map<Long, List<Long>> likesFilm = new HashMap<>();
    // Хранилище ид фильма и количества лайков
    private final Map<Long, Integer> likesUser = new HashMap<>();
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilm(filmId);

        if (film == null) {
            log.info("Фильм {} не найден", filmId);
            throw new NotFoundException("Фильм не найден");
        }

        if (userStorage.getUserById(userId) == null) {
            log.info("Пользователь {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }

        if (!likesUser.containsKey(filmId)) {
            likesUser.put(filmId, 0);
            film.setLikes(new HashSet<>());
        }

        if (!likesFilm.containsKey(userId)) {
            likesFilm.put(userId, new ArrayList<>());
        }

        if (likesFilm.get(userId).contains(filmId)) {
            log.debug("Пользователь {} уже лайкнул фильм {}", userId, filmId);
            throw new ValidationException("Пользователь уже лайкнул фильм");
        }

        likesFilm.get(userId).add(filmId);

        log.info("Фильм {} добавлен в хранилище лайков", filmId);

        // Увеличиваем количество лайков для фильма
        likesUser.put(filmId, film.getLikes().size());
        log.info("Количество лайков фильма {} = {}", filmId, likesUser.get(filmId));
    }

    public void deleteLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilm(filmId);

        if (!likesFilm.containsKey(userId)
                && !likesFilm.get(userId).contains(filmId)
                && !film.getLikes().contains(userId)) {
            log.debug("Пользователь {} не лайкнул фильм {}", userId, filmId);
            throw new NotFoundException("Пользователя нет в списке фильмов, отмеченных лайками");
        }

        if (!likesUser.containsKey(filmId)) {
            log.debug("Фильм {} не найден в хранилище лайков", filmId);
            throw new NotFoundException("Фильма нет в списке фильмов, отмеченных лайками");
        }

        // Удаляем фильм из списка фильмов с лайками пользователя
        likesFilm.get(userId).remove(filmId);

        // Уменьшаем количество лайков для фильма
        likesUser.put(filmId, likesUser.get(filmId) - 1);
        film.getLikes().remove(userId);
    }

    // Вывод популярных фильмов
    public List<Film> getPopular(Integer count) {
        if (!likesUser.isEmpty()) {
            return likesUser.entrySet()
                    .stream()
                    .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                    .limit(count)
                    .map(entry -> getFilmById(entry.getKey()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        log.debug("Список фильмов с лайками пуст");
        throw new NotFoundException("Список фильмов с лайками пуст");
    }

    // Получение фильма по ид
    private Film getFilmById(Long filmId) {
        return filmStorage.getFilm(filmId);
    }
}
