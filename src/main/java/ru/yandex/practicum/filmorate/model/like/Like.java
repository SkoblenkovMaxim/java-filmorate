package ru.yandex.practicum.filmorate.model.like;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Like {

    private Long id;
    private Long filmId;
    private Long userId;
}
