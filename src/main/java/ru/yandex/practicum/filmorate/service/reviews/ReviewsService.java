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

        //checkAddReviews(reviews);

        Reviews rev = reviewsStorage.addReviews(reviews);

        if (reviews != null) {

            if (reviews.getIsPositive() != null) {
                if (reviews.getIsPositive()) {
                    reviewsStorage.addLike(reviews.getFilmId(), reviews.getUserId());
                } else {
                    reviewsStorage.addDislike(reviews.getFilmId(), reviews.getUserId());
                }
            } else {
                throw new ValidationException("Оценка отзыва не может быть пустой");
            }

            // Установлено ноль по умолчанию при создании отзыва
            rev.setUseful(0);

            return reviewsMapper.toReviewsDto(rev);
        }

        throw new ValidationException("Отзыв не может быть пустым");
    }

    public ReviewsDto updateReviews(ReviewsDto reviewsDto) {
        Reviews reviews = reviewsMapper.toReviews(reviewsDto);

        Reviews reviewsFromDb = reviewsStorage.getReviews(reviews.getReviewId());

        if (reviewsFromDb != null) {
            return reviewsMapper.toReviewsDto(reviewsStorage.updateReviews(reviews));
        } else {
            throw new ValidationException("Отзыв не найден");
        }
    }

    public void deleteReviews(Long id) {
        reviewsStorage.deleteReviews(id);
    }

    public ReviewsDto getReviews(Long id) {
        return reviewsMapper.toReviewsDto(reviewsStorage.getReviews(id));
    }

    public List<Reviews> getReviewsByFilmId(Long filmId, Long count) {
        return reviewsStorage.getReviewsByFilmId(filmId, count);
    }

    public void addLike(Long id, Long userId) {

        // Проверка наличия лайка пользователя
        if (reviewsStorage.getReviews(id).getUserId() != null) {
            throw new ValidationException("Пользователь с userId " + userId + " уже оценил фильм с id " + id);
        }
        reviewsStorage.addLike(id, userId);
    }

    public void addDislike(Long id, Long userId) {

        // Проверка наличия дизлайка пользователя
        if (reviewsStorage.getReviews(id).getUserId() != null) {
            throw new ValidationException("Пользователь с userId " + userId + " уже оценил фильм с id " + id);
        }
        reviewsStorage.addDislike(id, userId);
    }

    public void deleteLike(Long id, Long userId) {

        // Проверка наличия лайка пользователя
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

    public void checkAddReviews(Reviews reviews) {

        log.debug("Начало проверки отзыва...");
        // Проверка контента
        log.debug("Проверка контента отзыва");
        if (reviews.getContent() == null || reviews.getContent().isBlank()) {
            throw new ValidationException("Отзыв не может быть пустым");
        }

        // Проверка положительности отзыва
        log.debug("Проверка положительности отзыва");
        if (reviews.getIsPositive() == null) {
            throw new ValidationException("Отзыв должен быть положительным или отрицательным");
        }

        // Проверка фильма
        log.debug("Проверка фильма отзыва");
        if (reviews.getFilmId() == null || filmService.getFilm(reviews.getFilmId()) == null) {
            throw new NotFoundException("Фильм не найден");
        }

        // Проверка пользователя
        log.debug("Проверка пользователя отзыва");
        if (reviews.getUserId() == null || userService.getUserById(reviews.getUserId()) == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        // Проверка наличия отзыва
        log.debug("Проверка наличия отзыва");
        if (reviewsStorage.getReviews(reviews.getReviewId()) != null) {
            throw new ValidationException("Отзыв с таким id уже существует");
        }

        // Проверка наличия пользователя в отзывах
        log.debug("Проверка наличия пользователя в отзывах");
        if (reviewsStorage.getReviews(reviews.getReviewId()).getUserId() != null) {
            throw new ValidationException("Пользователь уже оставил отзыв");
        }
    }
}
