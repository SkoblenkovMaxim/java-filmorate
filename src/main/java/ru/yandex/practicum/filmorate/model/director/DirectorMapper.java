package ru.yandex.practicum.filmorate.model.director;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DirectorMapper {

    DirectorDto toDirectorDto(Director director);

    Director toDirector(DirectorDto directorDto);

}
