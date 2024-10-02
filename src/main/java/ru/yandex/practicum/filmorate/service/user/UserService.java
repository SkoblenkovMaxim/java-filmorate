package ru.yandex.practicum.filmorate.service.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserStorage userStorage;

    private final Map<Long, Set<Long>> friendsList = new HashMap<>(); // Список друзей

    public User createUser(User user) {
        User createdUser = userStorage.createUser(user);
        friendsList.put(createdUser.getId(), new HashSet<>());
        return createdUser;
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Long userId) {
        return userStorage.getUserById(userId);
    }

    public void removeUser(Long userId) {
        userStorage.removeUser(userId);
    }

    // добавление в друзья
    public void addFriends(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            log.debug("Нельзя добавить самого себя в друзья");
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }

        if (userStorage.getUserById(friendId) == null) {
            log.debug("Пользователь с id={} не найден", friendId);
            throw new NotFoundException("Пользователь не найден");
        }

        if (!friendsList.isEmpty()) {
            if (friendsList.get(userId) != null && friendsList.get(userId).contains(friendId)) {
                log.debug("Пользователь с id={} уже у вас в друзьях", friendId);
                throw new DuplicatedDataException("Пользователь уже добавлен в друзья");
            }
        }

        friendsList.get(userId).add(friendId);
        log.info("Пользователь с id={} добавлен в список ваших друзей", friendId);
        friendsList.get(friendId).add(userId);
        log.info("Вы теперь друзья с id={}", userId);
    }

    // удаление из друзей
    public void deleteFriend(Long userId, Long friendId) {
        if (friendsList.containsKey(userId)) {
            if (friendsList.get(userId).contains(friendId)) {
                friendsList.get(userId).remove(friendId);
                log.info("Пользователь с id={} удален из списка ваших друзей", friendId);
            }
        } else {
            log.debug("Пользователь с id={} не найден", userId);
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }

        if (friendsList.containsKey(friendId)) {
            if (friendsList.get(friendId).contains(userId)) {
                friendsList.get(friendId).remove(userId);
                log.info("Пользователь с id={} удален из списка ваших друзей", friendId);
            }
        } else {
            log.debug("Пользователь с id={} не найден", friendId);
            throw new NotFoundException("Пользователь с id=" + friendId + " не найден");
        }
    }

    // вывод списка друзей
    public Collection<User> getAllFriends(Long userId) {
        if (friendsList.containsKey(userId)) {
            List<User> allFriends = new ArrayList<>();
            for (Long friendId : friendsList.get(userId)) {
                if (userStorage.getUsers().contains(userStorage.getUserById(friendId))) {
                    allFriends.add(userStorage.getUserById(friendId));
                }
            }
            log.info("Вывод списка друзей пользователя {}", userId);
            return allFriends;
        }
        log.debug("У пользователя {} нет друзей", userId);
        throw new NotFoundException("У вас нет друзей.");
    }

    // вывод списка общих друзей
    public List<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        List<User> firstUser = (List<User>) getAllFriends(firstUserId);
        List<User> secondUser = (List<User>) getAllFriends(secondUserId);
        List<User> commonFriends = new ArrayList<>();

        if (isValidUser(firstUserId) || isValidUser(secondUserId)) {
            log.debug("Пользователь с id={} не найден", firstUserId);
            throw new NotFoundException("Пользователь с id=" + firstUserId + " не найден");
        }
        log.info("Получение списка общих друзей пользователей {} и {}", firstUserId, secondUserId);
        for (User user : firstUser) {
            if (secondUser.contains(user)) {
                commonFriends.add(user);
            }
        }
        return commonFriends;
    }

    // Проверка наличия пользователя в хранилище
    public boolean isValidUser(Long userId) {
        return userStorage.getUserById(userId) == null;
    }
}
