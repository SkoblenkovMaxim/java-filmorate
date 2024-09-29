package ru.yandex.practicum.filmorate.model.film;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Getter
@Setter
public class Film {
    private Long id; // целочисленный идентификатор
    @NotBlank (message = "Название фильма не может быть пустым")
    private String name; // название
    @Size(max = 200, message = "Описание фильма должно быть меньше 200 символов")
    private String description; // описание
    private LocalDate releaseDate; // дата релиза
    @Positive(message = "Продолжительность фильма не может быть отрицательным значением")
    private int duration; // продолжительность фильма
    private Set<Long> likes;

//    public Long getLikes() {
//        return likes++;
//    }
}
