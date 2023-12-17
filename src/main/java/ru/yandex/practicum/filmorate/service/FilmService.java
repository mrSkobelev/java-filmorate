package ru.yandex.practicum.filmorate.service;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

@Slf4j
@Service
@AllArgsConstructor
public class FilmService {
    private final FilmStorage storage;

    public Film getFilmById(int filmId) {
        return storage.getFilmById(filmId);
    }

    public List<Film> getAllFilms() {
        return storage.getAllFilms();
    }

    public Film createFilm(Film film) {
        return storage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return storage.updateFilm(film);
    }

    public void addLike(int filmId, int userId) {
        storage.addLike(filmId, userId);
        log.info("Лайк успешно добавлен для фильма с id: " + filmId);
    }

    public void removeLike(int filmId, int userId) {
        if (getFilmById(filmId) == null) {
            throw new DataNotFoundException("Не найден фильм, для которого требуется удалить лайк");
        }

        storage.removeLike(filmId, userId);
        log.info("Лайк успешно удален для фильма с id: " + filmId);
    }

    public List<Film> getTop10Films(int count) {
        return storage.getTopFilms(count);
    }
}
