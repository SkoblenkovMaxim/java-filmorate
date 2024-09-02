package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class UserController {
    private final Map<Long, User> allUsers = new HashMap<>();

    // Счетчик id
    public Long countIdUser() {
        long count = allUsers.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++count;
    }

    // Создание пользователя
    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User newUser) {
        if (allUsers.values().stream()
                .anyMatch(user -> user.getEmail().equals(newUser.getEmail()))) {
            log.error("Этот email уже используется");
            throw new ValidationException("Этот email уже используется");
        }

        newUser.setId(countIdUser());
        allUsers.put(newUser.getId(), newUser);
        return newUser;
    }

    // Обновление пользователя
    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User newUser) {
        if (newUser.getName().isBlank() || newUser.getName().isEmpty()) {
            log.error("Имя не может быть null");
        }
        if (allUsers.containsKey(newUser.getId())) {
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
        throw new ValidationException("id " + newUser.getId() + " не найден");
    }

    // Получение списка всех пользователей
    @GetMapping("/users")
    public Collection<User> getAllUsers() {
        return allUsers.values();
    }
}
