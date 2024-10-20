package ru.yandex.practicum.filmorate.model.genre;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GenreMapper {

    GenreDto toGenreDto(Genre genre);

    Genre toGenre(GenreDto genreDto);
}
