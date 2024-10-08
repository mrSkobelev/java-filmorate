package ru.yandex.practicum.filmorate.storage.inMemory;

import java.time.LocalDate;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Slf4j

public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long generatorId = 1;

    @Override
    public User getUserById(Integer id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new DataNotFoundException("Не найден пользователь с id: " + id);
        }
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Текущее количество пользователей: {}", users.size());

        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) {
        validUser(user);

        if (users.containsKey(user.getId())) {
            throw new UserAlreadyExistException("Пользователь с id "
                + user.getId() + " уже зарегистрирован.");
        }

        //user.setId(generateId());

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        log.info("Добавлен пользователь {}", user.getEmail());

        //users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        validUser(user);

        if (!(users.containsKey(user.getId()))) {
            log.info("Не найден пользователь при попытке обновления");
            throw new DataNotFoundException("Не найден пользователь с id " + user.getId());
        }

        log.info("Обновлён пользователь {}", user.getEmail());

        //users.put(user.getId(), user);

        return user;
    }

    @Override
    public void addFriend(int userId, int friendId) {

    }

    @Override
    public List<User> getUserFriends(int userId) {
        return null;
    }

    @Override
    public void removeFriend(int userId, int friendId) {

    }

    @Override
    public List<User> commonFriends(int userId, int friendId) {
        return null;
    }

    private void validUser(User user) {
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
