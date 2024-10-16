package ru.yandex.practicum.filmorate.model.genre;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Genre {
    private Long id;
    private Long filmId;
    private Long genreId;
}