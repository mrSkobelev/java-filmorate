package ru.yandex.practicum.filmorate.storage.db;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.db.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private FilmDbStorage filmDbStorage;

    Mpa mpaG = Mpa.builder().id(1).name("G").build();
    Mpa mpaPg = Mpa.builder().id(2).name("PG").build();
    LocalDate releaseDate1 = LocalDate.of(2012, 11, 15);
    LocalDate releaseDate2 = LocalDate.of(2010, 12, 14);

    Film film1 = new Film(1, "Первый фильм", "Описание первого фильма", releaseDate1, 120, mpaG);
    Film film2 = new Film(2, "Второй фильм", "Описание второго фильма", releaseDate2, 130, mpaPg);


    @BeforeEach
    public void setUp() {
        GenreDbStorage genreDbStorage = new GenreDbStorage(jdbcTemplate);
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        filmDbStorage = new FilmDbStorage(jdbcTemplate, genreDbStorage, userDbStorage);
    }

    @Test
    @DirtiesContext
    public void testCreateFilm() {
        Film film = filmDbStorage.createFilm(film1);

        Assertions.assertThat(film)
            .usingRecursiveComparison()
            .isEqualTo(film1);
    }

    @Test
    @DirtiesContext
    public void testGetFilmById() {
        filmDbStorage.createFilm(film1);
        Film film = filmDbStorage.getFilmById(film1.getId());

        Assertions.assertThat(film)
            .usingRecursiveComparison()
            .isEqualTo(film1);
    }

    @Test
    @DirtiesContext
    public void testGetAllFilms() {
        filmDbStorage.createFilm(film1);
        filmDbStorage.createFilm(film2);

        List<Film> filmList = new ArrayList<>(filmDbStorage.getAllFilms());

        Assertions.assertThat(filmList.size()).isEqualTo(2);
    }

    @Test
    @DirtiesContext
    public void testUpdateFilm() {
        Film film = film1;
        filmDbStorage.createFilm(film);

        film.setName("Новое название");
        filmDbStorage.updateFilm(film);

        Assertions.assertThat(filmDbStorage.getFilmById(film.getId()).getName())
            .isEqualTo("Новое название");
    }
}
