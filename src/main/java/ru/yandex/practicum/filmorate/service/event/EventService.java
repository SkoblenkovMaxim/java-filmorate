package ru.yandex.practicum.filmorate.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventStorage eventStorage;
    public List<Event> getAllUserEvents(Long userId) {
        return eventStorage.getAllUserEvents(userId);
    }

}
