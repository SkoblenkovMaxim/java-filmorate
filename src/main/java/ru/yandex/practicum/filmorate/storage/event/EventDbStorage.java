package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventOperation;
import ru.yandex.practicum.filmorate.model.event.EventType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;


    public EventDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Event> getAllUserEvents(Long userId) {
        String query = "SELECT * FROM events WHERE user_id = ?";
        return jdbcTemplate.query(query, this::mapEvent, userId);
    }



    @Override
    public void addEvent(Event event) {
        String query = """
                INSERT INTO events (event_type, event_operation, user_id, entity_id, event_timestamp)
                VALUES (?,?,?,?,?);
                """;
        jdbcTemplate.update(query,
                event.getEventType().toString(), event.getOperation().toString(), event.getUserId(),
                event.getEntityId(), new Timestamp(event.getTimestamp()));

    }

    private Event mapEvent(ResultSet rs, int rowNum) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("event_timestamp");
        Long timestampMillis = timestamp != null ? timestamp.getTime() : null;
        return Event.builder()
                .id(rs.getLong("event_id"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(EventOperation.valueOf(rs.getString("event_operation")))
                .entityId(rs.getLong("entity_id"))
                .userId(rs.getLong("user_id"))
                .timestamp(timestampMillis)
                .build();
    }
}