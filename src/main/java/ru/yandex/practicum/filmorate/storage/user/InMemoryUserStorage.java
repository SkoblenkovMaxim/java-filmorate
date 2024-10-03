package ru.yandex.practicum.filmorate.storage.user;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.IdGenerator;

@Slf4j
@RequiredArgsConstructor
@Component
public class InMemoryUserStorage implements UserStorage {

    @Getter
    private final Map<Long, User> allUsers = new HashMap<>();

    private final IdGenerator idGenerator;

    // Создание пользователя
    public User createUser(User newUser) {
        if (allUsers.values().stream()
                .anyMatch(user -> user.getEmail().equals(newUser.getEmail()))) {
            log.error("Этот email уже используется");
            throw new ValidationException("Этот email уже используется");
        }

        newUser.setId(idGenerator.getNextId());
        if (newUser.getName() == null) {
            newUser.setName(newUser.getLogin());
        }
        allUsers.put(newUser.getId(), newUser);
        return newUser;
    }

    // Обновление пользователя
    public User updateUser(User newUser) {
        if (newUser.getName().isEmpty()) {
            log.error("Имя не может быть null");
            throw new ValidationException("Имя не может быть null");
        }
        if (isValidUser(newUser.getId())) {
            User oldUser = allUsers.get(newUser.getId());
            if (newUser.getEmail() != null) {
                oldUser.setEmail(newUser.getEmail());
            }
            if (newUser.getName() != null) {
                oldUser.setName(newUser.getName());
            }
            if (newUser.getLogin() != null) {
                oldUser.setLogin(newUser.getLogin());
            }
            if (newUser.getBirthday() != null) {
                oldUser.setBirthday(newUser.getBirthday());
            }
            return oldUser;
        }
        log.debug("id={} не найден", newUser.getId());
        throw new NotFoundException("id " + newUser.getId() + " не найден");
    }

    // Получение списка всех пользователей
    public Collection<User> getUsers() {
        return allUsers.values();
    }

    // Получение пользователя по id
    public User getUserById(Long userId) {
        if (isValidUser(userId)) {
            return allUsers.get(userId);
        }
        log.debug("Пользователь с id={} не найден", userId);
        throw new NotFoundException("Пользователь с id=" + userId + " не найден");
    }

    // Удаление пользователя
    public void removeUser(Long userId) {
        if (isValidUser(userId)) {
            allUsers.remove(userId, allUsers.get(userId));
        } else {
            log.debug("Пользователь {} не найден", allUsers.get(userId).getName());
            throw new NotFoundException("Пользователь не найден");
        }
    }

    public boolean isValidUser(Long userId) {
        return allUsers.containsKey(userId);
    }
}
