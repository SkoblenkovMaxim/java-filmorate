package ru.yandex.practicum.filmorate.model.genre;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Genre {
    private Integer genreId;
    private String genreName;
}
