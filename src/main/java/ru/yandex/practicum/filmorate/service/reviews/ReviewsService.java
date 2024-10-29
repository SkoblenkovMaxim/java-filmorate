package ru.yandex.practicum.filmorate.service.reviews;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.reviews.Reviews;
import ru.yandex.practicum.filmorate.model.reviews.ReviewsDto;
import ru.yandex.practicum.filmorate.model.reviews.ReviewsMapper;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.reviews.ReviewsStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReviewsService {

    private final ReviewsStorage reviewsStorage;
    private final FilmService filmService;
    private final UserService userService;
    private final ReviewsMapper reviewsMapper;

    public ReviewsService(@Qualifier("reviewsDbStorage") ReviewsStorage reviewsStorage,
                          FilmService filmService,
                          UserService userService,
                          ReviewsMapper reviewsMapper) {
        this.reviewsStorage = reviewsStorage;
        this.filmService = filmService;
        this.userService = userService;
        this.reviewsMapper = reviewsMapper;
    }

    public ReviewsDto addReviews(ReviewsDto reviewsDto) {
        Reviews reviews = reviewsMapper.toReviews(reviewsDto);

        // Установлено ноль по умолчанию при создании отзыва
        reviews.setUseful(reviewsStorage.setUsefulScore(reviews.getReviewId()));

        if (reviews.getIsPositive()) {
            reviewsStorage.addLike(reviews.getFilmId(), reviews.getUserId());
        } else {
            reviewsStorage.addDislike(reviews.getFilmId(), reviews.getUserId());
        }

        if (filmService.getFilm(reviews.getFilmId()) == null) {
            throw new NotFoundException("Фильм не найден");
        }

        if (userService.getUserById(reviews.getUserId()) == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        Reviews rev = reviewsStorage.addReviews(reviews);

        return reviewsMapper.toReviewsDto(rev);
    }

    public ReviewsDto updateReviews(ReviewsDto reviewsDto) {
        Reviews reviews = reviewsMapper.toReviews(reviewsDto);

        reviews.setUseful(reviewsStorage.setUsefulScore(reviews.getReviewId()));

        Reviews reviewsFromDb = reviewsStorage.getReviews(reviews.getReviewId());
        reviewsFromDb.setContent(reviews.getContent());
        reviewsFromDb.setIsPositive(reviews.getIsPositive());

        return reviewsMapper.toReviewsDto(reviewsStorage.updateReviews(reviewsFromDb));
    }

    public void deleteReviews(Long id) {
        reviewsStorage.deleteReviews(id);
    }

    public ReviewsDto getReviews(Long id) {

        if (reviewsMapper.toReviewsDto(reviewsStorage.getReviews(id)) != null) {
            return reviewsMapper.toReviewsDto(reviewsStorage.getReviews(id));
        }
        throw new NotFoundException("Отзыв не найден");
    }

    public List<Reviews> getReviewsByFilmId(Long filmId, Long count) {

        List<Reviews> reviews;

        if (filmId == null) {
            reviews = reviewsStorage.getAllReviews();
        } else {
            reviews = reviewsStorage.getReviewsByFilmId(filmId);
        }

        if (reviews.size() > count) {
            reviews = reviews.stream().limit(count).collect(Collectors.toList());
        }

        return reviews;
    }

    public void addLike(Long id, Long userId) {

        if (reviewsStorage.getReviews(id) != null) {
            reviewsStorage.addLike(id, userId);
        } else {
            throw new NotFoundException("Отзыв не найден");
        }
    }

    public void addDislike(Long id, Long userId) {

        if (reviewsStorage.getReviews(id) != null) {
            reviewsStorage.addDislike(id, userId);
        } else {
            throw new NotFoundException("Отзыв не найден");
        }
    }

    public void deleteLike(Long id, Long userId) {

        if (reviewsStorage.getReviews(id).getUserId() == null) {
            throw new ValidationException("Пользователь с userId " + userId + " уже оценил фильм с id " + id);
        }
        reviewsStorage.deleteLike(id, userId);
    }

    public void deleteDislike(Long id, Long userId) {

        // Проверка наличия дизлайка пользователя
        if (reviewsStorage.getReviews(id).getUserId() == null) {
            throw new ValidationException("Пользователь с userId " + userId + " уже оценил фильм с id " + id);
        }
        reviewsStorage.deleteDislike(id, userId);
    }
}
