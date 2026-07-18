package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    @Override
    public User create(User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        log.info("Создан пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        users.put(user.getId(), user);
        log.info("Обновлён пользователь: {}", user);
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Optional<User> findById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void addFriend(int userId, int friendId) {
        User user = users.get(userId);
        if (user == null) throw new NotFoundException("Пользователь с id " + userId + " не найден");
        User friend = users.get(friendId);
        if (friend == null) throw new NotFoundException("Пользователь с id " + friendId + " не найден");
        user.getFriends().add(friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        User user = users.get(userId);
        if (user == null) throw new NotFoundException("Пользователь с id " + userId + " не найден");
        user.getFriends().remove(friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        User user = users.get(userId);
        if (user == null) throw new NotFoundException("Пользователь с id " + userId + " не найден");
        return user.getFriends().stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        User user = users.get(userId);
        User other = users.get(otherId);
        if (user == null) throw new NotFoundException("Пользователь с id " + userId + " не найден");
        if (other == null) throw new NotFoundException("Пользователь с id " + otherId + " не найден");
        return user.getFriends().stream()
                .filter(id -> other.getFriends().contains(id))
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}