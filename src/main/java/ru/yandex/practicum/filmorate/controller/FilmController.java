package ru.yandex.practicum.filmorate.controller;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.FilmNameAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.InvalidFilmNameException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private Map<String, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Текущее количество фильмов: {}", films.size());

        return films.values();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        validFilm(film);

        if (films.containsKey(film.getName())) {
            throw new FilmNameAlreadyExistException("Фильм с названием " + film.getName()
                + " уже существует.");
        }

        log.info("Добавлен фильм {}", film.getName());

        films.put(film.getName(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        validFilm(film);

        log.info("Обновлён фильм {}", film.getName());

        films.put(film.getName(), film);

        return film;
    }

    private void validFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.info("Отсутствует название фильма");
            throw new InvalidFilmNameException("Название фильма не может быть пустым.");
        }

        char[] descriptionChars = film.getDescription().toCharArray();
        if (descriptionChars.length > 200) {
            log.info("Длинное описание фильма.");
            throw new ValidationException("Количество символов в описании должно быть не больше 200");
        }

        LocalDate validationDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(validationDate)) {
            log.info("Слишком ранняя дата релиза.");
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895");
        }

        if (film.getDuration() < 0) {
            log.info("Отрицательная продолжительность фильма.");
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
    }
}
