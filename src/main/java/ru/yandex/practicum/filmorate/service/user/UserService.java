package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserStorage userStorage;
    private final Map<Long, Set<Long>> friendsList = new HashMap<>(); // Список друзей

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
    public User addFriends(Long userId, Long friendId) {
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
        return userStorage.getUserById(userId);
    }
    // удаление из друзей
    public void deleteFriend(Long userId, Long friendId) {
        if (friendsList.get(userId).contains(friendId)) {
            friendsList.get(userId).remove(friendId);
            log.info("Пользователь с id={} удален из списка ваших друзей", friendId);
        }
        if (!friendsList.containsKey(userId)) {
            log.debug("Пользователь {} не найден", friendId);
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
