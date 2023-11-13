package ru.yandex.practicum.filmorate.controller;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.FilmNameAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.InvalidFilmNameException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;
    private final FilmStorage storage;

    @Autowired
    public FilmController(FilmService service, FilmStorage storage) {
        this.service = service;
        this.storage = storage;
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) {
        return storage.getFilmById(id);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return storage.getAllFilms();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        return storage.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        return storage.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        service.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        service.removeLike(id, userId);
    }

    @GetMapping("/popular?count={count}")
    public List<Film> getTop10Films(@RequestParam(required = false) final Integer count) {
        if (count == null || count <= 0) {
            return service.getTop10Films(10);
        } else {
            return service.getTop10Films(count);
        }
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(final NotFoundException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler({ValidationException.class,
        FilmNameAlreadyExistException.class,
        InvalidFilmNameException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidException(final RuntimeException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleServerException(final RuntimeException e) {
        return Map.of("serverError", e.getMessage());
    }
}