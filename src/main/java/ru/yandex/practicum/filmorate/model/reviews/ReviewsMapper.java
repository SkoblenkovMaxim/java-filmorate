package ru.yandex.practicum.filmorate.model.reviews;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReviewsMapper {

    ReviewsDto toReviewsDto(Reviews reviews);

    Reviews toReviews(ReviewsDto reviewsDto);
}
