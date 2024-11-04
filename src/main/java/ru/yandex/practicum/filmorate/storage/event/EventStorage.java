package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventOperation;

import java.util.List;

public interface EventStorage {
    List<Event> getAllUserEvents(Long userId);

    void createFriendEvent(Long userId, Long friendId, EventOperation eventOperation);

    void createReviewEvent(Long userId, Long reviewId, EventOperation eventOperation);

    void createLikeEvent(Long filmId, Long userId, EventOperation eventOperation);

}
