package ru.yandex.practicum.filmorate.model.rating;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class Rating {
    private Integer ratingId;
    private String nameRating;
}
