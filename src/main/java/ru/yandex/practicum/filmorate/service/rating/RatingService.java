package ru.yandex.practicum.filmorate.service.rating;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.rating.Rating;
import ru.yandex.practicum.filmorate.storage.raiting.RatingStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RatingService {
    private final RatingStorage ratingStorage;

    public List<Rating> getAllRating() {
        return ratingStorage.getAllRating()
                .stream()
                .sorted(Comparator.comparing(Rating::getId).reversed())
                .collect(Collectors.toList());
    }

    public Rating getRatingById(Integer id) {
        return ratingStorage.getRatingById(id);
    }
}
