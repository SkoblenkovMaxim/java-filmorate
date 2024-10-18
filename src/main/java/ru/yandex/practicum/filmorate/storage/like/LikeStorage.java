package ru.yandex.practicum.filmorate.storage.like;

import java.util.List;

import ru.yandex.practicum.filmorate.model.like.Like;

public interface LikeStorage {

    void createLike(Like like); // добавление пользователя

    void removeLike(Long filmId, Long userId); // удаление пользователя

    List<Like> getAllLikes(); // Получение всех пользователей
}
