package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {

    User getUserById(Integer id);

    List<User> getAllUsers();

    User createUser(User user);

    User updateUser(User user);

    void addFriend(int userId, int friendId);

    List<User> getUserFriends(int userId);

    void removeFriend(int userId, int friendId);

    List<User> commonFriends(int userId, int friendId);
}
