package ru.yandex.practicum.filmorate.model.director;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Director {

    private Long id;
    @NotBlank(message = "Имя режиссера не может быть пустым")
    private String name;
}
