package ru.yandex.practicum.filmorate.model.rating;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RatingMapper {

    RatingDto ratingDto(Rating rating);

    Rating dtoRating(RatingDto ratingDto);
}
