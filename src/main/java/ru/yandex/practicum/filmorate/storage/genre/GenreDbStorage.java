package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbc;

    @Override
    public Collection<Genre> findAll() {
        return jdbc.query("SELECT * FROM genres ORDER BY id", this::mapRowToGenre);
    }

    @Override
    public Optional<Genre> findById(int id) {
        List<Genre> genres = jdbc.query("SELECT * FROM genres WHERE id = ?", this::mapRowToGenre, id);
        return genres.stream().findFirst();
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("id"), rs.getString("name"));
    }
}