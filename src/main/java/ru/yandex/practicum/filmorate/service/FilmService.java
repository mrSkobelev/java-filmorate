package ru.yandex.practicum.filmorate.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public void addLike(long filmId, long userId) {
        Film film = storage.getFilmById(filmId);

        Set<Long> filmLikes = film.getLikes();
        filmLikes.add(userId);
        film.setLikes(filmLikes);

        storage.updateFilm(film);
        log.info("Лайк успешно добавлен для фильма с id: " + filmId);
    }

    public void removeLike(long filmId, long userId) {
        Film film = storage.getFilmById(filmId);

        Set<Long> filmLikes = film.getLikes();
        if (filmLikes.contains(userId)) {
            filmLikes.remove(userId);
        }

        storage.updateFilm(film);
        log.info("Лайк успешно добавлен для фильма с id: " + filmId);
    }

    public List<Film> getTop10Films(int count) {
        List<Film> top10 = new ArrayList<>();

        storage.getAllFilms().stream()
            .sorted(Comparator.comparing(film -> film.getLikes().size()))
            .limit(count)
            .sorted(Collections.reverseOrder())
            .map(film -> top10.add(film));

        return top10;
    }

}
