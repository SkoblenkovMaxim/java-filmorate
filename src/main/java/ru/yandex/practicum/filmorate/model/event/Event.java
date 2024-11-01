package ru.yandex.practicum.filmorate.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
public class Event {
    private Long eventId;
    private EventType eventType;
    private EventOperation operation;
    private Long userId;
    private Long entityId;
    private Long timestamp;
}