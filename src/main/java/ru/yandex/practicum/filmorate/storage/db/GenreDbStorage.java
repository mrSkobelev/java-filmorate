package ru.yandex.practicum.filmorate.storage.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

@Slf4j
@Component
@AllArgsConstructor
public class GenreDbStorage {
    JdbcTemplate jdbcTemplate;

    public Genre getGenreById(int genreId) {
        try {
            String sql = "SELECT * FROM genres WHERE genre_id = ?";
            Genre genre = jdbcTemplate.queryForObject(sql, getGenreMapper(), genreId);
            log.info("Получен из БД жанр с id = {}", genreId);
            return genre;
        } catch (RuntimeException e) {
            log.warn("Не найден в БД жанр с id = {}", genreId);
            throw new DataNotFoundException("Не найден в БД жанр с id = " + genreId);
        }

    }

    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql, getGenreMapper());
    }

    public List<Genre> getFilmGenres(int filmId) {
        String sql = "SELECT * FROM genres WHERE genre_id IN "
            + "(SELECT genre_id FROM film_genre WHERE film_id = ?) ORDER BY genre_id";

        return jdbcTemplate.query(sql, getGenreMapper(), filmId);
    }

    public void addGenres(Film film) {
        List<Genre> genres = new ArrayList<>(film.getGenres());

        String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?);";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@NonNull PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, film.getId());
                ps.setInt(2, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
    }

    public void updateGenres(Film film) {
        String sql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getId());
        addGenres(film);
        log.info("Обновлёны в БД жанры фильма с id = {}: ", film.getId());
    }

    private static RowMapper<Genre> getGenreMapper() {
        return ((rs, rowNum) -> Genre.builder()
            .id(rs.getInt("genre_id"))
            .name(rs.getString("genre_name"))
            .build());
    }
}
