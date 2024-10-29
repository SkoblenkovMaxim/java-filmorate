package ru.yandex.practicum.filmorate.model.director;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectorDto {

    private Long id;
    private String name;
}
