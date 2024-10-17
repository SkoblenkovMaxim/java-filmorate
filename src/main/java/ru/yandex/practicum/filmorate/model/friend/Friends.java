package ru.yandex.practicum.filmorate.model.friend;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Friends {
    @NotNull
    private Long userId;
    @NotNull
    private Long friendId;
    boolean isFriendStatus;
}
