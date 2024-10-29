package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.model.event.Event;

import java.util.List;

public interface EventStorage {
    List<Event> getAllUserEvents(Long userId);
    void addEvent(Event event);

}