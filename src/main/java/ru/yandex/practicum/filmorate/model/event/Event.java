package ru.yandex.practicum.filmorate.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
public class Event {
    Long eventId;
    EventType eventType;
    EventOperation operation;
    Long userId;
    Long entityId;
    Long timestamp;
}