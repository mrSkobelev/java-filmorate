package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {

    Film getFilmById(long filmId);

    List<Film> getAllFilms();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    long generateId();
}
