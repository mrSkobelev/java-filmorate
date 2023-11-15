package ru.yandex.practicum.filmorate;

import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

public class UserControllerTest {
    UserStorage storage;
    UserService service;
    UserController controller;

    @BeforeEach
    public void setUp() {
        storage = new InMemoryUserStorage();
        service = new UserService(storage);
        controller = new UserController(service);
    }

    @Test
    public void createValidUser() {
        User user = User.builder()
            .id(1)
            .email("test@mail.ru")
            .login("testLogin")
            .name("testName")
            .birthday(LocalDate.of(1990, 12, 12))
            .build();

        controller.createUser(user);

        Assertions.assertTrue(controller.getAllUsers().contains(user));
    }

    @Test
    public void createUserWithLoginAndWithoutName() {
        User user = User.builder()
            .id(1)
            .email("test@mail.ru")
            .login("testLogin")
            .name("")
            .birthday(LocalDate.of(1990, 12, 12))
            .build();

        controller.createUser(user);

        Assertions.assertTrue(controller.getAllUsers().contains(user));
    }

    @Test
    public void createUserWithNameAndWithoutLogin() {
        User user = User.builder()
            .id(1)
            .email("test@mail.ru")
            .login("")
            .name("testName")
            .birthday(LocalDate.of(1990, 12, 12))
            .build();

        controller.createUser(user);

        Assertions.assertTrue(controller.getAllUsers().contains(user));
    }

    @Test
    public void createUserWithoutLoginAndWithoutName() {
        User user = User.builder()
            .id(1)
            .email("test@mail.ru")
            .login("")
            .name("")
            .birthday(LocalDate.of(1990, 12, 12))
            .build();

        Throwable throwable = Assertions.assertThrows(ValidationException.class, () -> {
            controller.createUser(user);
        });

        Assertions.assertEquals(ValidationException.class, throwable.getClass());
    }

    @Test
    public void createDuplicateValidUser() {
        User user = User.builder()
            .id(1)
            .email("test@mail.ru")
            .login("testLogin")
            .name("testName")
            .birthday(LocalDate.of(1990, 12, 12))
            .build();

        controller.createUser(user);

        Throwable throwable = Assertions.assertThrows(UserAlreadyExistException.class, () -> {
            controller.createUser(user);
        });

        Assertions.assertEquals(UserAlreadyExistException.class, throwable.getClass());
    }

    @Test
    public void createUserWithoutEmail() {
        User user = User.builder()
            .id(1)
            .email("")
            .login("testLogin")
            .name("testName")
            .birthday(LocalDate.of(1990, 12, 12))
            .build();

        Throwable throwable = Assertions.assertThrows(ValidationException.class, () -> {
            controller.createUser(user);
        });

        Assertions.assertEquals(ValidationException.class, throwable.getClass());
    }
}
