package ru.yandex.practicum.filmorate.storage.db;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private UserDbStorage userDbStorage;

    LocalDate birthday1 = LocalDate.of(1990, 11, 12);
    LocalDate birthday2 = LocalDate.of(1991, 12, 15);
    User user1 = User.builder()
        .id(1)
        .email("first@mail.com")
        .login("firstLogin")
        .name("firstName")
        .birthday(birthday1)
        .build();

    User user2 = User.builder()
        .id(2)
        .email("second@mail.com")
        .login("secondLogin")
        .name("secondName")
        .birthday(birthday2)
        .build();

    @BeforeEach
    public  void setUp() {
        userDbStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    @DirtiesContext
    public void testCreateUser() {
        User user = userDbStorage.createUser(user1);

        Assertions.assertThat(user)
            .usingRecursiveComparison()
            .isEqualTo(user1);
    }

    @Test
    @DirtiesContext
    public void testGetUserById() {
        userDbStorage.createUser(user1);

        User user = userDbStorage.getUserById(user1.getId());

        Assertions.assertThat(user)
            .usingRecursiveComparison()
            .isEqualTo(user1);
    }

    @Test
    @DirtiesContext
    public void testGetAllUsers() {
        userDbStorage.createUser(user1);
        userDbStorage.createUser(user2);

        List<User> userList = new ArrayList<>(userDbStorage.getAllUsers());

        Assertions.assertThat(userList.size())
            .isEqualTo(2);
    }

    @Test
    @DirtiesContext
    public void testUpdateUser() {
        User user = userDbStorage.createUser(user1);

        user.setLogin("NewSecondLogin");

        Assertions.assertThat(userDbStorage.getUserById(user.getId()).getLogin())
            .isEqualTo("NewSecondLogin");
    }

}
