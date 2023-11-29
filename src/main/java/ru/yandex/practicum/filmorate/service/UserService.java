package ru.yandex.practicum.filmorate.service;

import java.util.ArrayList;
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

    public User getUserById(long id) {
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

    public void addFriend(long userId, long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        user.getFriendsId().add(friendId);
        friend.getFriendsId().add(userId);

        updateUser(user);
        updateUser(friend);

        log.info("пользователи с id " + userId + " и " + friendId + " добавились в друзья");
    }

    public List<User> getUserFriends(long userId) {
        List<User> userFriends = new ArrayList<>();
        User user = getUserById(userId);

        for (Long friendId : user.getFriendsId()) {
            userFriends.add(getUserById(friendId));
        }

        return userFriends;
    }

    public void removeFriend(long userId, long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        user.getFriendsId().remove(friendId);
        friend.getFriendsId().remove(userId);

        updateUser(user);
        updateUser(friend);

        log.info("пользователи с id " + userId + " и " + friendId + " добавились в друзья");
    }

    public List<User> commonFriends(long userId, long friendId) {
        List<User> commonFriends = new ArrayList<>();

        User user = getUserById(userId);
        User friend = getUserById(friendId);

        for (Long id : user.getFriendsId()) {
            if (friend.getFriendsId().contains(id)) {
                commonFriends.add(getUserById(id));
            }
        }

        return commonFriends;
    }
}
