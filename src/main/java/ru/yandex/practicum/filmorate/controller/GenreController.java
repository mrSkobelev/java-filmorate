package ru.yandex.practicum.filmorate.controller;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.db.GenreDbStorage;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/genres")
public class GenreController {
    private final GenreDbStorage genreDbStorage;

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable int id) {
        return genreDbStorage.getGenreById(id);
    }

    @GetMapping()
    public List<Genre> getAllGenres() {
        return genreDbStorage.getAllGenres();
    }
}
