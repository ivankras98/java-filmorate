package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbc;

    @Override
    public Collection<Mpa> findAll() {
        return jdbc.query("SELECT * FROM mpa_ratings ORDER BY id", this::mapRowToMpa);
    }

    @Override
    public Optional<Mpa> findById(int id) {
        List<Mpa> list = jdbc.query("SELECT * FROM mpa_ratings WHERE id = ?", this::mapRowToMpa, id);
        return list.stream().findFirst();
    }

    private Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getInt("id"), rs.getString("name"), rs.getString("description"));
    }
}