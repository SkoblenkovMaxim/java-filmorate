package ru.yandex.practicum.filmorate.service.director;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.director.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorStorage directorStorage;

    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        getDirectorOrThrow(director.getId());
        return directorStorage.updateDirector(director);
    }

    public void deleteDirector(Long directorId) {
        getDirectorOrThrow(directorId);
        directorStorage.deleteDirector(directorId);
    }

    public Director getDirectorById(Long directorId) {
        return getDirectorOrThrow(directorId);
    }

    public Collection<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    private Director getDirectorOrThrow(Long directorId) {
        Director director = directorStorage.getDirectorById(directorId);

        if (director == null) {
            throw new NotFoundException("Режиссер с ID " + directorId + " не найден");
        }
        return director;
    }
}
