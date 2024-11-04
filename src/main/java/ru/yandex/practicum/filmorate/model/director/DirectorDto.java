package ru.yandex.practicum.filmorate.model.director;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectorDto {
    private Long id;
    @NotBlank(message = "Имя режиссера не может быть пустым")
    private String name;
}
