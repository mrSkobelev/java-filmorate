package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {

    User getUserById(long id);

    List<User> getAllUsers();

    User createUser(User user);

    User updateUser(User user);

    long generateId();
}
