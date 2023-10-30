package ru.yandex.practicum.filmorate.controller;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private Map<String, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Текущее количество пользователей: {}", users.size());

        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        validUser(user);

        if (users.containsKey(user.getEmail())) {
            throw new UserAlreadyExistException("Пользователь с электронной почтой "
                + user.getEmail() + " уже зарегистрирован.");
        }

        log.info("Добавлен пользователь {}", user.getEmail());

        users.put(user.getEmail(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        validUser(user);

        log.info("Обновлён пользователь {}", user.getEmail());

        users.put(user.getEmail(), user);

        return user;
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
