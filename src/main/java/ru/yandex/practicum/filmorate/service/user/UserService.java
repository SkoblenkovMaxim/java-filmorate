package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final Map<Long, Set<Long>> friendsList = new HashMap<>(); // Список друзей

    public UserService() {
        userStorage = new InMemoryUserStorage();
    }


    public User createUser(User user) {
        return userStorage.createUser(user);
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

    public void removeUser(User user) {
        userStorage.removeUser(user);
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
            if (friendsList.get(userId).contains(friendId)) {
                log.debug("Пользователь с id={} уже у вас в друзьях", friendId);
                throw new DuplicatedDataException("Пользователь уже добавлен в друзья");
            }
        }
        if (!friendsList.containsKey(userId)) {
            friendsList.put(userId, new HashSet<>());
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
            log.debug("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
    }

    // вывод списка общих друзей
    public Collection<User> getAllFriends(Long userId) {
        if (friendsList.containsKey(userId)) {
            List<User> allFriends = new ArrayList<>();
            for (Long friendId : friendsList.get(userId)) {
                if (userStorage.getUsers().contains(userStorage.getUserById(friendId))) {
                    allFriends.add(userStorage.getUserById(friendId));
                }
            }
            log.info("Вывод списка общих друзей пользователя {}", userId);
            return allFriends;
        }
        log.debug("У пользователя {} нет друзей", userId);
        throw new NotFoundException("У вас нет друзей.");
    }
}
