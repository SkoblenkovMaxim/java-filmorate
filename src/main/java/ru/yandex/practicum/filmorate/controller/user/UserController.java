package ru.yandex.practicum.filmorate.controller.user;

import java.util.Collection;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.FilmDto;
import ru.yandex.practicum.filmorate.model.user.UserDto;
import ru.yandex.practicum.filmorate.service.user.UserService;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto user) {
        return userService.createUser(user);
    }

    @PutMapping
    public UserDto updateUser(@Valid @RequestBody UserDto user) {
        return userService.updateUser(user);
    }

    @GetMapping
    public Collection<UserDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@Valid @PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@Valid @PathVariable Long userId) {
        userService.removeUser(userId);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriends(@Valid @PathVariable Long userId, @Valid @PathVariable Long friendId) {
        userService.addFriends(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFriend(@Valid @PathVariable Long userId, @Valid @PathVariable Long friendId) {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<UserDto> getAllFriends(@Valid @PathVariable Long userId) {
        return userService.getFriendsByUserId(userId);
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    public List<UserDto> getCommonFriends(@Valid @PathVariable Long id, @Valid @PathVariable Long friendId) {
        return userService.getCommonFriends(id, friendId);
    }

    @GetMapping("/{id}/recommendations")
    public List<FilmDto> getUsersRecommendations(@PathVariable("id") long userId) {
        return userService.getUsersRecommendations(userId);
    }
}
