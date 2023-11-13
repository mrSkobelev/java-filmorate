package ru.yandex.practicum.filmorate.exception;

public class FilmNameAlreadyExistException extends RuntimeException {
    public FilmNameAlreadyExistException(String s) {
        super(s);
    }
}
