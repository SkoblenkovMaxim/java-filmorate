package ru.yandex.practicum.filmorate.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventOperation;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventStorage eventStorage;
    private final UserStorage userStorage;

    public List<Event> getAllUserEvents(Long userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("User " + userId + " is not found");
        }
        return eventStorage.getAllUserEvents(userId);
    }

    public void createFriendEvent(Long userId, Long friendId, EventOperation eventOperation) {
        userValidCheck(userId);
        eventStorage.createFriendEvent(userId, friendId, eventOperation);
    }

    public void createReviewEvent(Long userId, Long reviewId, EventOperation eventOperation) {
        userValidCheck(userId);
        eventStorage.createReviewEvent(userId, reviewId, eventOperation);
    }

    public void createLikeEvent(Long filmId, Long userId, EventOperation eventOperation) {
        userValidCheck(userId);
        eventStorage.createLikeEvent(filmId, userId, eventOperation);
    }

    private void userValidCheck(Long userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("User " + userId + " is not found");
        }
    }

}
