package ru.yandex.practicum.filmorate.controller.rating;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.rating.RatingDto;
import ru.yandex.practicum.filmorate.service.rating.RatingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class RatingController {

    private final RatingService ratingService;

    @GetMapping
    public List<RatingDto> getAllRating() {
        return ratingService.getAllRating();
    }

    @GetMapping("/{id}")
    public RatingDto getRatingById(@Valid @PathVariable int id) {
        return ratingService.getRatingById(id);
    }
}
