package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.Collection;

public interface UserStorage {

    User createUser(User user); // добавление пользователя

    void removeUser(Long userId); // удаление пользователя

    User updateUser(User user); //модификация, обновление пользователя

    Collection<User> getUsers(); // Получение всех пользователей

    User getUserById(Long userId); // Получение пользователя по id

    boolean isContains(Long id); // Проверка наличия пользователя
}
