package ru.yandex.practicum.filmorate.model.film;

import jakarta.validation.constraints.*;

import lombok.*;
import ru.yandex.practicum.filmorate.model.director.Director;
import ru.yandex.practicum.filmorate.model.genre.Genre;
import ru.yandex.practicum.filmorate.model.rating.Rating;
import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Setter
@Data
@AllArgsConstructor
public class Film {
    private Long id; // целочисленный идентификатор
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name; // название
    @Size(min = 1, max = 200, message = "Описание фильма должно быть больше 1 и меньше 200 символов")
    private String description; // описание
    private LocalDate releaseDate; // дата релиза
    @Positive(message = "Продолжительность фильма не может быть отрицательным значением")
    private int duration; // продолжительность фильма
    private int likeCount;
    private Rating mpa; // рейтинг
    private List<Genre> genres; // жанр
    private List<Director> directors;
}
