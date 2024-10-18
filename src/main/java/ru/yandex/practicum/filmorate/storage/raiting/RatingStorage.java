package ru.yandex.practicum.filmorate.storage.raiting;

import ru.yandex.practicum.filmorate.model.rating.Rating;

import java.util.List;

public interface RatingStorage {

    Rating getRatingById(Integer ratingId);

    List<Rating> getAllRating();
}
