package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNameAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.InvalidFilmNameException;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final int descriptionLength = 200;
    private final LocalDate validationDate = LocalDate.of(1895, 12, 28);

    private final Map<Long, Film> films = new HashMap<>();
    private long generatorId = 1;

    @Override
    public Film getFilmById(long filmId) {
        if (films.containsKey(filmId)) {
            return films.get(filmId);
        }
        throw new DataNotFoundException("Не найден фильм с id: " + filmId);
    }

    @Override
    public List<Film> getAllFilms() {
        log.info("Текущее количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film createFilm(Film film) {
        validFilm(film);

        if (films.containsKey(film.getId())) {
            log.info("Попытка создать фильм с существующим id");
            throw new FilmNameAlreadyExistException("Фильм с id " + film.getId()
                + " уже существует.");
        }

        film.setId(generateId());

        log.info("Добавлен фильм {}", film.getName());

        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        validFilm(film);

        if (!(films.containsKey(film.getId()))) {
            log.info("Не найден фильм при попытке обновления");
            throw new DataNotFoundException("Не найден фильм с id " + film.getId());
        }

        log.info("Обновлён фильм {}", film.getName());

        films.put(film.getId(), film);

        return film;
    }

    private void validFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.info("Отсутствует название фильма");
            throw new InvalidFilmNameException("Название фильма не может быть пустым.");
        }

        String descriptionChars = film.getDescription();
        if (descriptionChars.length() > descriptionLength) {
            log.info("Длинное описание фильма.");
            throw new ValidationException("Количество символов в описании должно быть не больше 200");
        }

        if (film.getReleaseDate().isBefore(validationDate)) {
            log.info("Слишком ранняя дата релиза.");
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895");
        }

        if (film.getDuration() < 0) {
            log.info("Отрицательная продолжительность фильма.");
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
    }

    @Override
    public long generateId() {
        return generatorId++;
    }
}
