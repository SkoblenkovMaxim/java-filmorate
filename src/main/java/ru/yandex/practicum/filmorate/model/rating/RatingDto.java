package ru.yandex.practicum.filmorate.model.rating;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class RatingDto {
    private Integer id;
    private String name;
    private String description;
}
