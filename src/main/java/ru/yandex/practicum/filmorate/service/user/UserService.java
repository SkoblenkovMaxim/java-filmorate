package ru.yandex.practicum.filmorate.service.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.friend.Friends;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import static java.lang.String.format;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
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

        checkIfFriend(userId, friendId);
        boolean isFriendStatus = friendStorage.isFriendStatus(userId, friendId);
        friendStorage.addFriend(userId, friendId, isFriendStatus);
    }

    // удаление из друзей
    public void deleteFriend(Long userId, Long friendId) {
        checkIfNotFriend(userId, friendId);
        friendStorage.deleteFriend(userId, friendId);
    }

    // вывод списка друзей
    public List<Long> getFriends(Long userId) {

        if (!getUsers().contains(getUserById(userId))) {
            throw new NotFoundException(format("Пользователь с id= %d не найден", userId));
        }

        List<Long> friends = friendStorage.getFriends(userId);
        log.trace("The user's friends list were returned: {}", friends);
        return friends;
    }

    public List<User> getFriendsByUserId(Long userId) {
        List<Friends> friends = friendStorage.getFriendsByUserId(userId);

        List<User> usersFromDb = new ArrayList<>();
        friends.forEach(friend -> {
            User user = userStorage.getUserById(friend.getFriendId());
            usersFromDb.add(user);
        });

        log.trace("The user's friends list were returned: {}", friends);
        return usersFromDb;
    }

    // вывод списка общих друзей
    public List<Long> getCommonFriends(Long firstUserId, Long secondUserId) {
        List<Long> firstUser = getFriends(firstUserId);
        List<Long> secondUser = getFriends(secondUserId);
        List<Long> commonFriends = new ArrayList<>();

        if (isValidUser(firstUserId) || isValidUser(secondUserId)) {
            log.debug("Пользователь с id={} не найден", firstUserId);
            throw new NotFoundException("Пользователь с id=" + firstUserId + " не найден");
        }
        log.info("Получение списка общих друзей пользователей {} и {}", firstUserId, secondUserId);
        for (Long userId : firstUser) {
            if (secondUser.contains(userId)) {
                commonFriends.add(userId);
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
        if (!getUsers().contains(getUserById(userId))) {
            throw new NotFoundException(format("User with id %d wasn't found", userId));
        }
        if (!getUsers().contains(getUserById(friendId))) {
            throw new NotFoundException(format("User with id %d wasn't found", userId));
        }
        if (userId.equals(friendId)) {
            throw new NotFoundException("Attempt to add yourself into a friends list, the id is " + userId);
        }
        if (friendStorage.isFriendStatus(userId, friendId)) {
            throw new RuntimeException(
                    format("The user with id %d is already friend of user with id %d", userId, friendId));
        }
    }

    private void checkIfNotFriend(Long userId, Long friendId) {
        log.debug("checkIfNotFriend({}, {})", userId, friendId);
        if (!getUsers().contains(getUserById(userId))) {
            throw new NotFoundException(format("User with id %d wasn't found", userId));
        }
        if (!getUsers().contains(getUserById(friendId))) {
            throw new NotFoundException(format("User with id %d wasn't found", userId));
        }
        if (userId.equals(friendId)) {
            throw new NotFoundException(
                    "Attempt to delete yourself from a friends list, the id is " + userId);
        }
        if (!friendStorage.isFriendStatus(userId, friendId)) {
            throw new NotFoundException(
                    format("There is no friendship between user with id %d and user with id %d", userId, friendId));
        }
    }
}
