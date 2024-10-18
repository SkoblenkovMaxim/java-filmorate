package ru.yandex.practicum.filmorate.service.rating;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.rating.Rating;
import ru.yandex.practicum.filmorate.storage.raiting.RatingStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RatingService {

    private final RatingStorage ratingStorage;

    public List<Rating> getAllRating() {
        return ratingStorage.getAllRating();
    }

    public Rating getRatingById(Integer id) {
        Rating rating = ratingStorage.getRatingById(id);
        if (rating == null) throw new NotFoundException("id " + id + " не найден");
        return rating;
    }
}
