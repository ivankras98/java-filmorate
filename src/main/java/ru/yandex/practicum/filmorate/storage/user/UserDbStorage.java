package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Slf4j
@Primary
@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbc;

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        log.info("Создан пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE id=?";
        int rows = jdbc.update(sql,
                user.getEmail(), user.getLogin(), user.getName(),
                Date.valueOf(user.getBirthday()), user.getId());
        if (rows == 0) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        log.info("Обновлён пользователь: {}", user);
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return jdbc.query("SELECT * FROM users", this::mapRowToUser);
    }

    @Override
    public Optional<User> findById(int id) {
        List<User> users = jdbc.query("SELECT * FROM users WHERE id = ?", this::mapRowToUser, id);
        return users.stream().findFirst();
    }

    @Override
    public void addFriend(int userId, int friendId) {
        // Дружба односторонняя — просто добавляем запись
        String sql = "INSERT INTO friendships (user_id, friend_id, status) VALUES (?, ?, 'UNCONFIRMED')";
        jdbc.update(sql, userId, friendId);
        log.info("Пользователь {} добавил в друзья {}", userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        jdbc.update("DELETE FROM friendships WHERE user_id = ? AND friend_id = ?", userId, friendId);
        log.info("Пользователь {} удалил из друзей {}", userId, friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        String sql = """
                SELECT u.* FROM users u
                JOIN friendships f ON u.id = f.friend_id
                WHERE f.user_id = ?
                """;
        return jdbc.query(sql, this::mapRowToUser, userId);
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        String sql = """
                SELECT u.* FROM users u
                JOIN friendships f1 ON u.id = f1.friend_id AND f1.user_id = ?
                JOIN friendships f2 ON u.id = f2.friend_id AND f2.user_id = ?
                """;
        return jdbc.query(sql, this::mapRowToUser, userId, otherId);
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        user.setFriends(new HashSet<>());
        return user;
    }
}