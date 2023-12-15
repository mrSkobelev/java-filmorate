package ru.yandex.practicum.filmorate.controller;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.db.MpaDbStorage;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/mpa")
public class MpaController {
    MpaDbStorage mpaDbStorage;

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable int id) {
        return mpaDbStorage.getMpaById(id);
    }

    @GetMapping
    public List<Mpa> getAllMpa() {
        return mpaDbStorage.getAllMpa();
    }

}
