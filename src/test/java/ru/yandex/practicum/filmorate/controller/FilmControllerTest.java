package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {
    @InjectMocks
    private FilmController filmController;

    private Film film;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        film = Film.builder()
                .name("Test Film")
                .description("This is a test film")
                .releaseDate(LocalDate.now()) // future date
                .duration(120)
                .build();
    }

    @Test
    void addFilm_ShouldAddFilmAndReturnIt() {
        Film addedFilm = filmController.addFilm(film);

        assertThat(addedFilm).isNotNull();
        assertThat(addedFilm.getId()).isNotNull();
        assertThat(addedFilm.getName()).isEqualTo(film.getName());
        assertThat(addedFilm.getDescription()).isEqualTo(film.getDescription());
    }

    @Test
    void updateFilm_ShouldUpdateExistingFilm() {
        Film addedFilm = filmController.addFilm(film);
        Long filmId = addedFilm.getId();

        Film updateFilm = Film.builder()
                .id(filmId)
                .name("Updated Film")
                .description("Updated description")
                .releaseDate(LocalDate.now())
                .duration(90)
                .build();

        Film updatedFilm = filmController.updateFilm(updateFilm);

        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated description");
    }

    @Test
    void updateFilm_ShouldThrowException_WhenFilmNotFound() {
        Film updateFilm = Film.builder()
                .id(999L) // Non-existent film id
                .name("Non-existent Film")
                .description("This film does not exist")
                .releaseDate(LocalDate.now())
                .duration(90)
                .build();

        ValidationException thrown = assertThrows(ValidationException.class, () -> {
            filmController.updateFilm(updateFilm);
        });

        assertThat(thrown.getMessage()).contains("Фильм 999 - Non-existent Film не найден");
    }

    @Test
    void getFilms_ShouldReturnAllFilms() {
        filmController.addFilm(film);
        Collection<Film> films = filmController.getFilms();

        assertThat(films).isNotEmpty();
        assertThat(films).hasSize(1);
        assertThat(films.iterator().next().getName()).isEqualTo(film.getName());
    }
}
