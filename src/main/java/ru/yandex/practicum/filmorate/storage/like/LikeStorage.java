package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class LikeStorage {
    // Хранилище ид пользователя и списка фильмов, которые он лайкнул
    private final Map<Long, List<Long>> likesFilm = new HashMap<>();
    // Хранилище ид фильма и количества лайков
    private final Map<Long, Long> likesUser = new HashMap<>();
    private FilmStorage filmStorage;

    public void addLike(Long filmId, Long userId) {
        // Добавляем лайк в список лайков пользователя
        likesFilm.computeIfAbsent(userId, k -> new ArrayList<>()).add(filmId);

        // Увеличиваем количество лайков для фильма
        likesUser.put(filmId, likesUser.getOrDefault(filmId, 0L) + 1);
    }

    public void deleteLike(Long filmId, Long userId) {
        // Проверяем, существует ли пользователь в хранилище лайков
        if (likesFilm.containsKey(userId)) {
            List<Long> userLikes = likesFilm.get(userId);
            if (userLikes.remove(filmId)) {
                // Уменьшаем количество лайков для фильма
                likesUser.put(filmId, likesUser.get(filmId) - 1);
                // Если количество лайков стало 0
                if (likesUser.get(filmId) <= 0) {
                    likesUser.remove(filmId);
                }
            }
        }
    }

    // Вывод популярных фильмов
    public List<Film> getPopular(Integer count) {
        // Сортируем фильмы по количеству лайков в порядке убывания и получаем топ count
        List<Film> popularFilms = likesUser.entrySet()
                .stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(count)
                .map(entry -> getFilmById(entry.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return popularFilms;
    }

    // Получение количества лайков фильма
    public List<Long> getLikes(Long filmId) {
        return likesUser.containsKey(filmId) ? Collections.singletonList(likesUser.get(filmId)) : Collections.emptyList();
    }

    // Получение фильма по ид
    private Film getFilmById(Long filmId) {
        return filmStorage.getFilm(filmId);
    }
}
