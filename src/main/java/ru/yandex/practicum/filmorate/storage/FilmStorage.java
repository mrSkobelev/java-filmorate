package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

public interface FilmStorage {

    Film getFilmById(int filmId);

    List<Film> getAllFilms();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    List<Film> getTop10Films(int count);
}
