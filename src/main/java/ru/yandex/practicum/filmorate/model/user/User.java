package ru.yandex.practicum.filmorate.model.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class User {
    private Long id; // целочисленный идентификатор
    @NotBlank
    @Email
    private String email; // электронная почта
    @NotBlank
    private String login; // логин пользователя
    private String name; // имя для отображения
    @Past
    private LocalDate birthday; // дата рождения
}
