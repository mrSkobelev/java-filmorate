package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(final DataNotFoundException e) {
        log.info("404 {}", e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler({ValidationException.class,
        FilmNameAlreadyExistException.class,
        InvalidFilmNameException.class,
        UserAlreadyExistException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidException(final RuntimeException e) {
        log.info("400 {}", e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleServerException(final RuntimeException e) {
        log.info("500 {}", e.getMessage());
        return Map.of("serverError", e.getMessage());
    }
}
