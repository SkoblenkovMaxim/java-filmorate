package ru.yandex.practicum.filmorate.storage.user;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.IdGenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    @Getter
    private final Map<Long, User> allUsers = new HashMap<>();
    private IdGenerator idGenerator = new IdGenerator();

    public InMemoryUserStorage(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    // Создание пользователя
    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User newUser) {
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
    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User newUser) {
        if (newUser.getName().isEmpty()) {
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
    public Collection<User> getUsers() {
        return allUsers.values();
    }

    // Получение пользователя по id
    @GetMapping("/users")
    public User getUserById(Long userId) {
        if (allUsers.containsKey(userId)) {
            return allUsers.get(userId);
        }
        log.debug("Пользователь с id={} не найден", userId);
        throw new NotFoundException("Пользователь с id=" + userId + " не найден");
    }

    // Удаление пользователя
    @GetMapping("/users")
    public void removeUser(User user) {
        if (allUsers.containsKey(user.getId())) {
            allUsers.remove(user.getId(), user);
        } else {
            log.debug("Пользователь {} не найден", user.getName());
            throw new NotFoundException("Пользователь не найден");
        }
    }
}
