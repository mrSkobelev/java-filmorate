package ru.yandex.practicum.filmorate.storage.inMemory;

import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.yandex.practicum.filmorate.exception.FilmNameAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.InvalidFilmNameException;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

@Slf4j

public class InMemoryFilmStorage implements FilmStorage {
    private static final int DESCRIPTION_LENGTH = 200;
    private static final LocalDate VALIDATION_DATE = LocalDate.of(1895, 12, 28);

    private final Map<Long, Film> films = new HashMap<>();
    private long generatorId = 1;

    @Override
    public Film getFilmById(int filmId) {
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

        //film.setId(generateId());

        log.info("Добавлен фильм {}", film.getName());

        //films.put(film.getId(), film);
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

        //films.put(film.getId(), film);

        return film;
    }

    @Override
    public void addLike(int filmId, int userId) {

    }

    @Override
    public void removeLike(int filmId, int userId) {

    }

    @Override
    public List<Film> getTop10Films(int count) {
        return null;
    }

    private void validFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.info("Отсутствует название фильма");
            throw new InvalidFilmNameException("Название фильма не может быть пустым.");
        }

        String descriptionChars = film.getDescription();
        if (descriptionChars.length() > DESCRIPTION_LENGTH) {
            log.info("Длинное описание фильма.");
            throw new ValidationException("Количество символов в описании должно быть не больше 200");
        }

        if (film.getReleaseDate().isBefore(VALIDATION_DATE)) {
            log.info("Слишком ранняя дата релиза.");
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895");
        }

        if (film.getDuration() < 0) {
            log.info("Отрицательная продолжительность фильма.");
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
    }
}
