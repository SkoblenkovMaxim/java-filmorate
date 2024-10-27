package ru.yandex.practicum.filmorate.model.reviews;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ReviewsDto {
    private long review_id; // Идентификатор отзыва
    @Length(min = 1, max = 255, message = "Отзыв должен содержать от 1 до 255 символов")
    @NotNull
    private String content; // Текст отзыва
    @NotNull
    private Boolean isPositive; // true - положительный, false - негативный
    @NotNull
    private Long user_id; // Идентификатор пользователя
    @NotNull
    private Long film_id; // Идентификатор фильма
    private Long useful; // Рейтинг полезности
}
