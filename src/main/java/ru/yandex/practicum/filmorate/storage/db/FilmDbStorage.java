package ru.yandex.practicum.filmorate.storage.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.InvalidFilmNameException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

@Slf4j
@RequiredArgsConstructor
@Component
public class FilmDbStorage implements FilmStorage {
    private static final int DESCRIPTION_LENGTH = 200;
    private static final LocalDate VALIDATION_DATE = LocalDate.of(1895, 12, 28);

    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;
    private final UserDbStorage userDbStorage;

    @Override
    public Film getFilmById(int filmId) {
        try {
            String sql = "SELECT "
                + "f.film_id,"
                + "f.film_name,"
                + "f.description,"
                + "f.release_date,"
                + "f.duration, "
                + "r.rating_id,"
                + "r.rating_name "
                + "FROM films AS f "
                + "JOIN rating AS r ON f.rating_id = r.rating_id "
                + "WHERE f.film_id = ?";
            Film film = jdbcTemplate.queryForObject(sql, getFilmMapper(), filmId);
            log.info("Получен фильм из БД с id = {}", filmId);
            return film;
        } catch (RuntimeException e) {
            log.warn("Не найден в БД фильм с id = {}", filmId);
            throw new DataNotFoundException("Не найден фильм с id = " + filmId);
        }
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT "
            + "f.film_id, "
            + "f.film_name, "
            + "f.description, "
            + "f.release_date, "
            + "f.duration, "
            + "r.rating_id, "
            + "r.rating_name "
            + "FROM films AS f "
            + "JOIN rating AS r ON f.rating_id = r.rating_id "
            + "ORDER BY f.film_id";
        return jdbcTemplate.query(sql, getFilmMapper());
    }

    @Override
    public Film createFilm(Film film) {
        validFilm(film);

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("films")
            .usingGeneratedKeyColumns("film_id");

        int id = insert.executeAndReturnKey(filmToMap(film)).intValue();
        film.setId(id);

        if (!film.getGenres().isEmpty()) {
            genreDbStorage.addGenres(film);
        }

        return film;
    }

    private static Map<String, Object> filmToMap(Film film) {
        return Map.of(
            "film_name", film.getName(),
            "description", film.getDescription(),
            "release_date", film.getReleaseDate(),
            "duration", film.getDuration(),
            "rating_id", film.getMpa().getId()
        );
    }

    @Override
    public Film updateFilm(Film film) {
        validFilm(film);

        if (getFilmById(film.getId()) == null) {
            log.warn("Не найден фильм с id = {}", film.getId());
            throw new DataNotFoundException("Не найден фильм с id = " + film.getId());
        }

        String sql = "UPDATE films SET "
            + "film_name = ?, "
            + "description = ?, "
            + "release_date = ?, "
            + "duration = ?, "
            + "rating_id = ? "
            + "WHERE film_id = ?";

        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(),
                            film.getDuration(), film.getMpa().getId(), film.getId());

        genreDbStorage.updateGenres(film);

        log.info("Обновлён фильм с id = " + film.getId());

        return getFilmById(film.getId());
    }

    @Override
    public void addLike(int filmId, int userId) {
        if (getFilmById(filmId) == null || userDbStorage.getUserById(userId) == null) {
            log.warn("Неверные параметры запроса при попытке поставить лайк");
            throw new DataNotFoundException("Неверные параметры запроса при попытке поставить лайк");
        }

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("likes");
        insert.execute(likesToMap(filmId, userId));
        log.info("Пользователь с id = {} добавил лайк фильму с id = {}", userId, filmId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        if (getFilmById(filmId) == null || userDbStorage.getUserById(userId) == null) {
            log.warn("Неверные параметры запроса при попытке удалить лайк");
            throw new DataNotFoundException("Неверные параметры запроса при попытке удалить лайк");
        }

        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    private static Map<String, Object> likesToMap(int filmId, int userId) {
        return Map.of(
            "film_id", filmId,
            "user_id", userId
        );
    }

    @Override
    public List<Film> getTopFilms(int count) {
        String sql = "SELECT count(user_id) AS count, f.*, r.rating_name "
            + "FROM likes l "
            + "RIGHT JOIN films f ON f.film_id = l.film_id  "
            + "JOIN rating r ON r.rating_id = f.rating_id "
            + "GROUP BY f.film_id, r.rating_id "
            + "ORDER BY count DESC "
            + "LIMIT ?";

        return jdbcTemplate.query(sql, getFilmMapper(), count);
    }

    protected RowMapper<Film> getFilmMapper() {
        return new RowMapper<Film>() {
            @Override
            public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
                Film film = new Film(
                    rs.getInt("film_id"),
                    rs.getString("film_name"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getInt("duration"),
                    Mpa.builder()
                        .id(rs.getInt("rating_id"))
                        .name(rs.getString("rating_name"))
                        .build()
                );
                film.getGenres().addAll(genreDbStorage.getFilmGenres(film.getId()));
                return film;
            }
        };
    }

    private void validFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.info("Отсутствует название фильма");
            throw new InvalidFilmNameException("Название фильма не может быть пустым.");
        }

        String descriptionChars = film.getDescription();
        if (descriptionChars.length() > DESCRIPTION_LENGTH) {
            log.info("Длинное описание фильма.");
            throw new ValidationException("Количество символов в описании должно быть не больше 200");
        }

        if (film.getReleaseDate().isBefore(VALIDATION_DATE)) {
            log.info("Слишком ранняя дата релиза.");
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895");
        }

        if (film.getDuration() < 0) {
            log.info("Отрицательная продолжительность фильма.");
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
    }
}
