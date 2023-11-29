package ru.yandex.practicum.filmorate;

import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.FilmNameAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.InvalidFilmNameException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

public class FilmControllerTest {
    FilmService service;
    FilmStorage storage;
    FilmController controller;

    @BeforeEach
    public void setUp() {
        storage = new InMemoryFilmStorage();
        service = new FilmService(storage);
        controller = new FilmController(service);
    }

    @Test
    public void createValidFilm() {
        Film film = Film.builder()
            .id(1)
            .name("testName")
            .description("testDescription")
            .duration(180)
            .releaseDate(LocalDate.of(1990, 12, 12))
            .build();

        controller.createFilm(film);

        Assertions.assertTrue(controller.getAllFilms().contains(film));
    }

    @Test
    public void createDuplicateFilm() {
        Film film = Film.builder()
            .id(1)
            .name("testName")
            .description("testDescription")
            .duration(180)
            .releaseDate(LocalDate.of(1990, 12, 12))
            .build();

        controller.createFilm(film);

        Throwable throwable = Assertions.assertThrows(FilmNameAlreadyExistException.class, () -> {
            controller.createFilm(film);
        });

        Assertions.assertEquals(FilmNameAlreadyExistException.class, throwable.getClass());
    }

    @Test
    public void createFilmWithoutName() {
        Film film = Film.builder()
            .id(1)
            .name("")
            .description("testDescription")
            .duration(180)
            .releaseDate(LocalDate.of(1990, 12, 12))
            .build();

        Throwable throwable = Assertions.assertThrows(InvalidFilmNameException.class, () -> {
            controller.createFilm(film);
        });

        Assertions.assertEquals(InvalidFilmNameException.class, throwable.getClass());
    }

    @Test
    public void createFilmWithLongDescription() {
        Film film = Film.builder()
            .id(1)
            .name("testName")
            .description("201symbols201symbols201symbols201symbols201symbols201symbols201symbols"
                + "201symbols201symbols201symbols201symbols201symbols201symbols201symbols201symbols"
                + "201symbols201symbols201symbols201symbols201symbols1")
            .duration(180)
            .releaseDate(LocalDate.of(1990, 12, 12))
            .build();

        Throwable throwable = Assertions.assertThrows(ValidationException.class, () -> {
            controller.createFilm(film);
        });

        Assertions.assertEquals(ValidationException.class, throwable.getClass());
    }

    @Test
    public void createFilmWithNegativeDuration() {
        Film film = Film.builder()
            .id(1)
            .name("testName")
            .description("testDescription")
            .duration(-1)
            .releaseDate(LocalDate.of(1990, 12, 12))
            .build();

        Throwable throwable = Assertions.assertThrows(ValidationException.class, () -> {
            controller.createFilm(film);
        });

        Assertions.assertEquals(ValidationException.class, throwable.getClass());
    }

    @Test
    public void createFilmWithInvalidReleaseDate() {
        Film film = Film.builder()
            .id(1)
            .name("testName")
            .description("testDescription")
            .duration(-1)
            .releaseDate(LocalDate.of(1895, 12, 27))
            .build();

        Throwable throwable = Assertions.assertThrows(ValidationException.class, () -> {
            controller.createFilm(film);
        });

        Assertions.assertEquals(ValidationException.class, throwable.getClass());
    }
}
