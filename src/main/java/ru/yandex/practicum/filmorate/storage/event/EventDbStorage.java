package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventOperation;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
public class EventDbStorage implements EventStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    @Autowired
    public EventDbStorage(JdbcTemplate jdbcTemplate, UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    @Override
    public List<Event> getAllUserEvents(Long userId) {

        if (userStorage.getUserById(userId) == null) {

            throw new NotFoundException("User " + userId + " is not found");
        }

        String query = "SELECT * FROM events WHERE user_id = ?";
        return jdbcTemplate.query(query, this::mapEvent, userId);
    }

    public void createFriendEvent(Long userId, Long friendId, EventOperation eventOperation) {

        Event event = Event.builder()
                .eventType(EventType.FRIEND)
                .operation(eventOperation)
                .entityId(friendId)
                .userId(userId)
                .timestamp(Timestamp.from(Instant.now()).getTime())
                .build();
        addEvent(event);
    }

    public void createLikeEvent(Long filmId, Long userId, EventOperation eventOperation) {

        Event event = Event.builder()
                .eventType(EventType.LIKE)
                .operation(eventOperation)
                .entityId(filmId)
                .userId(userId)
                .timestamp(Timestamp.from(Instant.now()).getTime())
                .build();
        addEvent(event);
    }

    public void createReviewEvent(Long userId, Long reviewId, EventOperation eventOperation) {

        Event event = Event.builder()
                .eventType(EventType.REVIEW)
                .operation(eventOperation)
                .entityId(reviewId)
                .userId(userId)
                .timestamp(Timestamp.from(Instant.now()).getTime())
                .build();
        addEvent(event);
    }

    private void addEvent(Event event) {

        if (userStorage.getUserById(event.getUserId()) == null) {

            throw new NotFoundException("User " + event.getUserId() + " is not found");
        }

        String query = """
                INSERT INTO events (event_type, event_operation, user_id, entity_id, event_timestamp)
                VALUES (?,?,?,?,?);
                """;
        jdbcTemplate.update(query,
                event.getEventType().toString(), event.getOperation().toString(), event.getUserId(),
                event.getEntityId(), new Timestamp(event.getTimestamp()));
    }

    private Event mapEvent(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getLong("event_id"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(EventOperation.valueOf(rs.getString("event_operation")))
                .entityId(rs.getLong("entity_id"))
                .userId(rs.getLong("user_id"))
                .timestamp(rs.getTimestamp("event_timestamp").getTime())
                .build();
    }
}
