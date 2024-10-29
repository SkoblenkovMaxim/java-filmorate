package ru.yandex.practicum.filmorate.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.sql.Timestamp;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
public class Event {
    Long id;
    EventType eventType;
    EventOperation operation;
    Long userId;
    Long entityId;
    Long timestamp;
}