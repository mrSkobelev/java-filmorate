package ru.yandex.practicum.filmorate.storage.db;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

@Slf4j
@Component
@AllArgsConstructor
public class MpaDbStorage {
    JdbcTemplate jdbcTemplate;

    public Mpa getMpaById(int id) {
        try {
            String sql = "SELECT * FROM rating WHERE rating_id = ?";
            Mpa mpa = jdbcTemplate.queryForObject(sql, getMpaMapper(), id);
            log.info("Получен из БД рейтинг с id = {}", id);
            return mpa;
        } catch (RuntimeException e) {
            log.warn("Не найден в БД рейтинг и id = {}", id);
            throw new DataNotFoundException("Не найден в БД рейтинг и id = " + id);
        }
    }

    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM rating";
        return jdbcTemplate.query(sql, getMpaMapper());
    }

    private static RowMapper<Mpa> getMpaMapper() {
        return ((rs, rowNum) -> Mpa.builder()
            .id(rs.getInt("rating_id"))
            .name(rs.getString("rating_name"))
            .build());
    }
}
