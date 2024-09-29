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

        //Film film = filmStorage.getFilm(filmId);
//        if (film == null && userStorage.getUserById(userId) == null) {
//            log.info("Пользователь {} или фильм {} не найден", filmId, userId);
//            throw new NotFoundException("Пользователь или фильм не найден");
//        }

//        if (likesFilm.containsKey(userId) && likesFilm.get(userId).contains(filmId)) {
//            log.debug("Пользователь {} уже лайкнул фильм {}", userId, filmId);
//            throw new ValidationException("Пользователь уже лайкнул фильм");
//        }

//        if (film.getLikes().contains(userId)) {
//            log.debug("Пользователь {} уже лайкнул фильм {}", userId, film.getId());
//            throw new ValidationException("Пользователь уже лайкнул фильм");
//        }

//        if (!likesFilm.containsKey(userId)) {
//            //likesFilm.get(userId).add(filmId);
//            likesFilm.put(userId, new ArrayList<>());
//            log.info("Фильм {} добавлен в хранилище лайков", filmId);
//            //likesFilm.computeIfAbsent(userId, k -> new ArrayList<>()).add(filmId);
//        }
//
//        if (!likesUser.containsKey(filmId)) {
//            likesUser.put(filmId, 0);
//        }
//
//        if (film != null) {
//            film.setLikes(new HashSet<>());
//        }

        //likesFilm.get(userId).add(filmId);
//        likesFilm.computeIfAbsent(userId, k -> new ArrayList<>()).add(filmId);


//        if (!likesUser.containsKey(filmId)) {
//            if (film != null) {
//                likesUser.put(filmId, 0L);
//                film.setLikes(new HashSet<>());
//
//                log.info("Фильм {} добавлен в хранилище лайков", filmId);
//            }
//        }
        // Добавляем лайк в список лайков пользователя
//        likesFilm.computeIfAbsent(userId, k -> new ArrayList<>()).add(filmId);

        //film.getLikes().add(userId);
        // Увеличиваем количество лайков для фильма
        likesUser.put(filmId, film.getLikes().size());
//        film.getLikes().add(userId);
        log.info("Количество лайков фильма {} = {}", filmId, likesUser.get(filmId));
//        log.info("Количество лайков фильма {} = {}", filmId, film.getLikes().size());
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

//        if (!likesUser.containsKey(filmId)) {
//            log.debug("Фильм {} не найден в хранилище лайков", filmId);
//            throw new NotFoundException("Фильма нет в списке фильмов, отмеченных лайками");
//        }

        // Удаляем фильм из списка фильмов с лайками пользователя
        likesFilm.get(userId).remove(filmId);

        // Уменьшаем количество лайков для фильма
        likesUser.put(filmId, likesUser.get(filmId) - 1);
        film.getLikes().remove(userId);

        // Проверяем, существует ли пользователь в хранилище лайков
//        if (likesFilm.containsKey(userId)) {
//            List<Long> userLikes = likesFilm.get(userId);
//            if (userLikes.remove(filmId)) {
//                // Уменьшаем количество лайков для фильма
//                likesUser.put(filmId, likesUser.get(filmId) - 1);
//                // Если количество лайков стало 0
//                if (likesUser.get(filmId) <= 0) {
//                    likesUser.remove(filmId);
//                }
//            }
//        }
//        log.debug("Пользователь с id={} не ставил лайки", userId);
//        throw new NotFoundException("Пользователя нет в списке фильмов, отмеченных лайками");
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
