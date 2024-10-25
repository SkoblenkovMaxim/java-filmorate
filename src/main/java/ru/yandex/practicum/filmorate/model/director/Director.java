package ru.yandex.practicum.filmorate.model.director;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Director {

    private Long id;
    private String name;
}
