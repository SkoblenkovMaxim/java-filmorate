package ru.yandex.practicum.filmorate.controller.reviews;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.reviews.Reviews;
import ru.yandex.practicum.filmorate.model.reviews.ReviewsDto;
import ru.yandex.practicum.filmorate.service.reviews.ReviewsService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/reviews")
public class ReviewsController {

    private final ReviewsService reviewsService;

    @PostMapping
    public ReviewsDto addReviews(@Valid @RequestBody ReviewsDto reviewsDto) {
        return reviewsService.addReviews(reviewsDto);
    }

    @PutMapping
    public ReviewsDto updateReviews(@Valid @RequestBody ReviewsDto reviewsDto) {
        return reviewsService.updateReviews(reviewsDto);
    }

    @DeleteMapping("/{id}")
    public void deleteReviews(@Valid @PathVariable Long id) {
        reviewsService.deleteReviews(id);
    }

    @GetMapping("/{id}")
    public ReviewsDto getReviews(@Valid @PathVariable Long id) {
        return reviewsService.getReviews(id);
    }

    @GetMapping
    public List<Reviews> getReviewsByFilmId(@Valid @RequestParam(defaultValue = "0") Long filmId,
                                            @Valid @RequestParam(defaultValue = "10") Long count) {
        return reviewsService.getReviewsByFilmId(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@Valid @PathVariable Long id, @Valid @PathVariable Long userId) {
        reviewsService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@Valid @PathVariable Long id, @Valid @PathVariable Long userId) {
        reviewsService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@Valid @PathVariable Long id, @Valid @PathVariable Long userId) {
        reviewsService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@Valid @PathVariable Long id, @Valid @PathVariable Long userId) {
        reviewsService.deleteDislike(id, userId);
    }
}
