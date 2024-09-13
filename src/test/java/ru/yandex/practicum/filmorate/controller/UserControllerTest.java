package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Проверка класса-контроллера пользователя")
class UserControllerTest {
    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void createUser_ShouldCreateUser_WhenValidUserProvided() {
        User newUser = User.builder()
                .email("user@example.com")
                .login("userLogin")
                .name("User Name")
                .birthday(LocalDate.now())
                .build();

        User createdUser = userController.createUser(newUser);

        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals(newUser.getEmail(), createdUser.getEmail());
        assertEquals(newUser.getLogin(), createdUser.getLogin());
    }

    @Test
    void createUser_ShouldThrowError_WhenEmailAlreadyExists() {
        User existingUser = User.builder()
                .email("user@example.com")
                .login("existingLogin")
                .name("Existing User")
                .birthday(LocalDate.now())
                .build();

        userController.createUser(existingUser);

        User newUserWithSameEmail = User.builder()
                .email("user@example.com")
                .login("newLogin")
                .name("New User")
                .birthday(LocalDate.now())
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.createUser(newUserWithSameEmail);
        });

        assertTrue(exception.getMessage().contains("Этот email уже используется"));
    }

    @Test
    void updateUser_ShouldUpdateUser_WhenValidUserProvided() {
        User existingUser = User.builder()
                .email("user@example.com")
                .login("userLogin")
                .name("User Name")
                .birthday(LocalDate.now())
                .build();

        userController.createUser(existingUser);

        User updateUser = User.builder()
                .id(existingUser.getId())
                .email("updated@example.com")
                .login("updatedLogin")
                .name("Updated User").birthday(LocalDate.now())
                .build();

        User updatedUser = userController.updateUser(updateUser);

        assertNotNull(updatedUser);
        assertEquals(updateUser.getEmail(), updatedUser.getEmail());
        assertEquals(updateUser.getLogin(), updatedUser.getLogin());
    }

    @Test
    void updateUser_ShouldThrowError_WhenUserNotFound() {
        User updateUser = User.builder()
                .id(999L) // ID, который не существует
                .email("updated@example.com")
                .login("updatedLogin")
                .name("Updated User")
                .birthday(LocalDate.now())
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.updateUser(updateUser);
        });

        assertTrue(exception.getMessage().contains("id 999 не найден"));
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        User user1 = User.builder()
                .email("user1@example.com")
                .login("userLogin1")
                .name("User One")
                .birthday(LocalDate.now())
                .build();

        User user2 = User.builder()
                .email("user2@example.com")
                .login("userLogin2")
                .name("User Two")
                .birthday(LocalDate.now())
                .build();

        userController.createUser(user1);
        userController.createUser(user2);

        Collection<User> users = userController.getAllUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
    }
}
