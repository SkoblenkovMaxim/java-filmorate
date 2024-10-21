package ru.yandex.practicum.filmorate.model.film;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FilmMapper {

    FilmDto toFilmDto(Film film);

    Film toFilm(FilmDto filmDto);

}
