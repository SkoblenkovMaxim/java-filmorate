package ru.yandex.practicum.filmorate.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventOperation;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventStorage eventStorage;

    public List<Event> getAllUserEvents(Long userId) {
        return eventStorage.getAllUserEvents(userId);
    }

    public void createFriendEvent(Long userId, Long friendId, EventOperation eventOperation) {
        eventStorage.createFriendEvent(userId, friendId, eventOperation);
    }

    public void createReviewEvent(Long userId, Long reviewId, EventOperation eventOperation) {
        eventStorage.createReviewEvent(userId, reviewId, eventOperation);
    }

    public void createLikeEvent(Long filmId, Long userId, EventOperation eventOperation) {
        eventStorage.createLikeEvent(filmId, userId, eventOperation);
    }

}
