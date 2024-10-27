package ru.yandex.practicum.filmorate.storage.reviews;

import ru.yandex.practicum.filmorate.model.reviews.Reviews;

import java.util.List;

public interface ReviewsStorage {

    Reviews addReviews(Reviews reviews);

    Reviews updateReviews(Reviews reviews);

    void deleteReviews(long idReviews);

    Reviews getReviews(long idReviews);

    List<Reviews> getReviewsByFilmId(Long idFilm);

    void addLike(Long idReviews, Long idUser);

    void addDislike(Long idReviews, Long idUser);

    void deleteLike(Long idReviews, Long idUser);

    void deleteDislike(Long idReviews, Long idUser);

    Integer setUsefulScore(Long idReviews);

    List<Reviews> getAllReviews();
}
