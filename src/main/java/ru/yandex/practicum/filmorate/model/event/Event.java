package ru.yandex.practicum.filmorate.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
public class Event {
    Long id;
    EventType eventType;
    EventOperation eventOperation;
    Long userId;
    Long entityId;
    LocalDateTime timestamp;
}

