package ru.yandex.practicum.filmorate.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Slf4j
@Service
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public User getUserById(int id) {
        return storage.getUserById(id);
    }

    public List<User> getAllUsers() {
        return storage.getAllUsers();
    }

    public User createUser(User user) {
        return storage.createUser(user);
    }

    public User updateUser(User user) {
        return storage.updateUser(user);
    }

    public void addFriend(int userId, int friendId) {
        storage.addFriend(userId, friendId);
    }

    public List<User> getUserFriends(int userId) {
        return storage.getUserFriends(userId);
    }

    public void removeFriend(int userId, int friendId) {
        storage.removeFriend(userId, friendId);
    }

    public List<User> commonFriends(int userId, int friendId) {
        return storage.commonFriends(userId, friendId);
    }
}
