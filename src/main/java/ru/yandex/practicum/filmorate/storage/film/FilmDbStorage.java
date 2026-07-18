package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.*;
import java.util.*;

@Slf4j
@Primary
@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbc;

    @Override
    public Film add(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) VALUES (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        saveGenres(film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name=?, description=?, release_date=?, duration=?, mpa_rating_id=? WHERE id=?";
        int rows = jdbc.update(sql,
                film.getName(), film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()), film.getDuration(),
                film.getMpa().getId(), film.getId());
        if (rows == 0) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        jdbc.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        saveGenres(film);
        log.info("Обновлён фильм: {}", film);
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        List<Film> films = jdbc.query("SELECT f.*, m.name AS mpa_name FROM films f " +
                "JOIN mpa_ratings m ON f.mpa_rating_id = m.id", this::mapRowToFilm);
        loadGenresForFilms(films);
        return films;
    }

    @Override
    public Optional<Film> findById(int id) {
        List<Film> films = jdbc.query(
                "SELECT f.*, m.name AS mpa_name FROM films f " +
                        "JOIN mpa_ratings m ON f.mpa_rating_id = m.id WHERE f.id = ?",
                this::mapRowToFilm, id);
        if (films.isEmpty()) return Optional.empty();
        loadGenresForFilms(films);
        return Optional.of(films.get(0));
    }

    @Override
    public void addLike(int filmId, int userId) {
        jdbc.update("INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)", filmId, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        jdbc.update("DELETE FROM film_likes WHERE film_id = ? AND user_id = ?", filmId, userId);
        log.info("Пользователь {} убрал лайк с фильма {}", userId, filmId);
    }

    @Override
    public List<Film> getPopular(int count) {
        String sql = """
                SELECT f.*, m.name AS mpa_name, COUNT(fl.user_id) AS likes_count
                FROM films f
                JOIN mpa_ratings m ON f.mpa_rating_id = m.id
                LEFT JOIN film_likes fl ON f.id = fl.film_id
                GROUP BY f.id
                ORDER BY likes_count DESC
                LIMIT ?
                """;
        List<Film> films = jdbc.query(sql, this::mapRowToFilm, count);
        loadGenresForFilms(films);
        return films;
    }

    private void saveGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) return;
        for (Genre genre : film.getGenres()) {
            jdbc.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)",
                    film.getId(), genre.getId());
        }
    }

    private void loadGenresForFilms(List<Film> films) {
        if (films.isEmpty()) return;
        String ids = films.stream()
                .map(f -> String.valueOf(f.getId()))
                .reduce((a, b) -> a + "," + b).orElse("");
        String sql = "SELECT fg.film_id, g.id, g.name FROM genres g " +
                "JOIN film_genres fg ON g.id = fg.genre_id WHERE fg.film_id IN (" + ids + ")";
        Map<Integer, Set<Genre>> genreMap = new HashMap<>();
        jdbc.query(sql, rs -> {
            int filmId = rs.getInt("film_id");
            Genre genre = new Genre(rs.getInt("id"), rs.getString("name"));
            genreMap.computeIfAbsent(filmId, k -> new LinkedHashSet<>()).add(genre);
        });
        for (Film film : films) {
            film.setGenres(genreMap.getOrDefault(film.getId(), new LinkedHashSet<>()));
        }
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa_rating_id"));
        mpa.setName(rs.getString("mpa_name"));
        film.setMpa(mpa);
        film.setGenres(new HashSet<>());
        film.setLikes(new HashSet<>());
        return film;
    }
}