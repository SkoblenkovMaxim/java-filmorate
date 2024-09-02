package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;

@Builder
@Getter
@Setter
public class Film {
    private Long id; // целочисленный идентификатор
    @NotBlank (message = "Название фильма не может быть пустым")
    private String name; // название
    @Size(max = 200, message = "Описание фильма должно быть меньше 200 символов")
    private String description; // описание
    @Future(message = "1895-12-28")
    private Instant releaseDate; // дата релиза
    @Positive(message = "Продолжительность фильма не может быть отрицательным значением")
    private Duration duration; // продолжительность фильма
}
