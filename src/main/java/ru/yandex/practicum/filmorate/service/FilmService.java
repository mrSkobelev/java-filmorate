package ru.yandex.practicum.filmorate.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage storage;

    @Autowired
    public FilmService(FilmStorage storage) {
        this.storage = storage;
    }

    public Film getFilmById(long filmId) {
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

    public void addLike(long filmId, long userId) {
        Film film = storage.getFilmById(filmId);

        film.getLikes().add(userId);

        updateFilm(film);
        log.info("Лайк успешно добавлен для фильма с id: " + filmId);
    }

    public void removeLike(long filmId, long userId) {
        Film film = getFilmById(filmId);

        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException("Не найден лайк от пользователя с id: " + userId);
        }

        film.getLikes().remove(userId);
        updateFilm(film);
        log.info("Лайк успешно удален для фильма с id: " + filmId);
    }

    public List<Film> getTop10Films(int count) {
        return getAllFilms().stream()
            .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
            .limit(count)
            .collect(Collectors.toList());
    }
}
