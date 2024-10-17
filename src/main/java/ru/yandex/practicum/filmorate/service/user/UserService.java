package ru.yandex.practicum.filmorate.service.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import static java.lang.String.format;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendDbStorage friendDbStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       FriendDbStorage friendDbStorage) {
        this.userStorage = userStorage;
        this.friendDbStorage = friendDbStorage;
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        User userFromDb = userStorage.getUserById(user.getId());
        if (userFromDb != null) {
            return userStorage.updateUser(user);
        } else {
            throw new NotFoundException("id " + user.getId() + " не найден");
        }
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

        checkIfFriend(userId, friendId);
        boolean isFriend = friendDbStorage.isFriendStatus(userId, friendId);
        friendDbStorage.addFriend(userId, friendId, isFriend);
    }

    // удаление из друзей
    public void deleteFriend(Long userId, Long friendId) {
        cheIfNotFriend(userId, friendId);
        friendDbStorage.deleteFriend(userId, friendId);
    }

    // вывод списка друзей
    public Collection<User> getAllFriends(Long userId) {

        if (!userStorage.isContains(userId)) {
            throw new NotFoundException(format("У вас нет друга %d", userId));
        }
        List<User> friends = friendDbStorage.getFriends(userId).stream()
                .mapToLong(Long::valueOf)
                .mapToObj(userStorage::getUserById)
                .collect(Collectors.toList());
        log.trace("The user's friends list were returned: {}", friends);
        return friends;
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

    //Проверка наличия пользователя в хранилище
    public boolean isValidUser(Long userId) {
        return userStorage.getUserById(userId) == null;
    }

    private void checkIfFriend(Long userId, Long friendId) {
        log.debug("checkIfFriend({}, {})", userId, friendId);
        if (!userStorage.isContains(userId)) {
            throw new NotFoundException(format("User with id %d wasn't found", userId));
        }
        if (!userStorage.isContains(friendId)) {
            throw new NotFoundException(format("User with id %d wasn't found", userId));
        }
        if (userId.equals(friendId)) {
            throw new DuplicatedDataException("Attempt to add yourself into a friends list, the id is " + userId);
        }
        if (friendDbStorage.isFriendStatus(userId, friendId)) {
            throw new ValidationException(
                    format("The user with id %d is already friend of user with id %d", userId, friendId));
        }
    }

    private void cheIfNotFriend(Long userId, Long friendId) {
        log.debug("checkIfNotFriend({}, {})", userId, friendId);
        if (!userStorage.isContains(userId)) {
            throw new ValidationException(format("User with id %d wasn't found", userId));
        }
        if (!userStorage.isContains(friendId)) {
            throw new NotFoundException(format("User with id %d wasn't found", userId));
        }
        if (userId.equals(friendId)) {
            throw new DuplicatedDataException(
                    "Attempt to delete yourself from a friends list, the id is " + userId);
        }
        if (!friendDbStorage.isFriendStatus(userId, friendId)) {
            throw new ValidationException(
                    format("There is no friendship between user with id %d and user with id %d", userId, friendId));
        }
    }
}
