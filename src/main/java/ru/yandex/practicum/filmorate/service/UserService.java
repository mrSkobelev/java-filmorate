package ru.yandex.practicum.filmorate.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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

    public void addFriend(long userId, long friendId) {
        User user = storage.getUserById(userId);
        User friend = storage.getUserById(friendId);

        Set<Long> usersFriends = user.getFriendsId();
        usersFriends.add(friendId);
        user.setFriendsId(usersFriends);

        Set<Long> friendFriends = friend.getFriendsId();
        friendFriends.add(userId);
        friend.setFriendsId(friendFriends);

        storage.updateUser(user);
        storage.updateUser(friend);

        log.info("пользователи с id " + userId + " и " + friendId + " добавились в друзья");
    }

    public void removeFriend(long userId, long friendId) {
        User user = storage.getUserById(userId);
        User friend = storage.getUserById(friendId);

        Set<Long> usersFriends = user.getFriendsId();

        if (!usersFriends.contains(friendId)) {
            throw new NotFoundException("Не найден пользователь с id: " + friendId +
                " в списке друзей");
        }

        usersFriends.remove(friendId);
        user.setFriendsId(usersFriends);

        Set<Long> friendFriends = friend.getFriendsId();
        friendFriends.remove(userId);
        friend.setFriendsId(friendFriends);

        storage.updateUser(user);
        storage.updateUser(friend);

        log.info("пользователи с id " + userId + " и " + friendId + " добавились в друзья");
    }

    public List<Long> commonFriends(long userId, long friendId) {
        List<Long> commonFriends = new ArrayList<>();

        User user = storage.getUserById(userId);
        User friend = storage.getUserById(friendId);

        for (Long id : user.getFriendsId()) {
            if (friend.getFriendsId().contains(id)) {
                commonFriends.add(id);
            }
        }

        return commonFriends;
    }
}
