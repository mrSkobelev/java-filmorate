package ru.yandex.practicum.filmorate.storage.db;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Slf4j
@AllArgsConstructor
@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User getUserById(Integer id) {
        try {
            String sql = "SELECT * FROM users WHERE user_id = ?";
            User user = jdbcTemplate.queryForObject(sql, getUserMapper(), id);
            log.info("Получен из БД пользователь с id = {}", id);
            return user;
        } catch (RuntimeException e) {
            log.warn("Не найден в БД пользователь с id = {}", id);
            throw new DataNotFoundException("Не найден в БД пользователь с id = " + id);
        }

    }

    private static RowMapper<User> getUserMapper() {
        return ((rs, rowNum) -> User.builder()
            .id(rs.getInt("user_id"))
            .email(rs.getString("email"))
            .login(rs.getString("login"))
            .name(rs.getString("user_name"))
            .birthday(rs.getDate("birthday").toLocalDate())
            .build());
    }

    @Override
    public List<User> getAllUsers() {
        return jdbcTemplate.query("SELECT * FROM users;", getUserMapper());
    }

    @Override
    public User createUser(User user) {
        validUser(user);

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("users")
            .usingGeneratedKeyColumns("user_id");

        int id = insert.executeAndReturnKey(userToMap(user)).intValue();
        user.setId(id);
        return user;
    }

    private static Map<String, Object> userToMap(User user) {
        return Map.of(
            "email", user.getEmail(),
            "login", user.getLogin(),
            "user_name", user.getName(),
            "birthday", user.getBirthday()
        );
    }

    @Override
    public User updateUser(User user) {
        validUser(user);

        if (getUserById(user.getId()) == null) {
            log.warn("Не найден пользователь с id = {}", user.getId());
            throw new DataNotFoundException("Не найден пользователь с id = " + user.getId());
        }

        String sql = "UPDATE users SET "
            + "email = ?, "
            + "login = ?, "
            + "user_name = ?, "
            + "birthday = ? "
            + "WHERE user_id = ?";

        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(),
            user.getBirthday(), user.getId());

        log.info("Успешно обновлён пользователь с id = {}", user.getId());
        return getUserById(user.getId());
    }

    @Override
    public void addFriend(int userId, int friendId) {
        if (getUserById(userId) == null || getUserById(friendId) == null) {
            log.warn("Не найден пользователь "
                + "при попытке добавления в друзья");
            throw new DataNotFoundException("Не найден пользователь "
                + "при попытке добавления в друзья");
        }

        String sql = "INSERT INTO friends VALUES(?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
        log.info("Пользователь с id = {} добавил пользователя с id = {} в друзья", userId, friendId);
    }

    @Override
    public List<User> getUserFriends(int userId) {
        String sql = "SELECT * FROM users WHERE user_id IN "
            + "(SELECT user_2 FROM friends WHERE user_1 = ?)";
        return jdbcTemplate.query(sql, getUserMapper(), userId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        String sql = "DELETE FROM friends WHERE user_1 = ? AND user_2 = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> commonFriends(int userId, int friendId) {
        List<User> userList = getUserFriends(userId);
        List<User> friendList = getUserFriends(friendId);
        List<User> common = new ArrayList<>();

        for (User u : userList) {
            if (friendList.contains(u)) {
                common.add(u);
            }
        }

        return common;
    }

    private void validUser(User user) {
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.info("Email пользователя не заполнен.");
            throw new ValidationException("Email должен быть заполнен.");
        }

        if (!(user.getLogin() == null || user.getLogin().isBlank())) {
            String[] login = user.getLogin().trim().split(" ");
            if (login.length > 1) {
                log.info("Пробелы в логине пользователя.");
                throw new ValidationException("Логин не должен содержать пробелы.");
            }
        }

        if ((user.getName() == null || user.getName().isBlank())
            && (user.getLogin() == null || user.getLogin().isBlank())) {
            log.info("Имя и логин пользователя пустые.");
            throw new ValidationException("Имя или логин должны быть заполнены.");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Дата рождения пользователя в будущем.");
            throw new ValidationException("Нельзя использовать дату рождения в будущем.");
        }
    }
}
