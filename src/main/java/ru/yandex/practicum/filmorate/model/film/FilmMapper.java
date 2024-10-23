package ru.yandex.practicum.filmorate.model.film;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.filmorate.model.director.DirectorMapper;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {DirectorMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface FilmMapper {

    FilmDto toFilmDto(Film film);

    Film toFilm(FilmDto filmDto);
}
