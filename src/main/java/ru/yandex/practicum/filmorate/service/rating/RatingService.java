package ru.yandex.practicum.filmorate.service.rating;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.rating.Rating;
import ru.yandex.practicum.filmorate.model.rating.RatingDto;
import ru.yandex.practicum.filmorate.model.rating.RatingMapper;
import ru.yandex.practicum.filmorate.storage.raiting.RatingStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RatingService {

    private final RatingStorage ratingStorage;
    private final RatingMapper ratingMapper;

    public List<RatingDto> getAllRating() {
        return ratingStorage.getAllRating().stream().map(ratingMapper::ratingDto).toList();
    }

    public RatingDto getRatingById(Integer id) {

        Rating rating = ratingStorage.getRatingById(id);
        if (rating == null) throw new NotFoundException("id " + id + " не найден");
        return ratingMapper.ratingDto(rating);
    }
}
