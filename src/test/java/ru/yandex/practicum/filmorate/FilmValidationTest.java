package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidationTest {

    private FilmController controller;

    @BeforeEach
    void setUp() {
        controller = new FilmController();
    }

    @Test
    @DisplayName("Добавить валидный фильм")
    void shouldAddValidFilm() {
        Film film = makeFilm("Название", "Описание", LocalDate.of(2000, 1, 1), 120);
        Film result = controller.addFilm(film);
        assertNotNull(result.getId());
    }

    @Test
    @DisplayName("Ошибка при пустом названии")
    void shouldFailOnEmptyName() {
        Film film = makeFilm("", "Описание", LocalDate.of(2000, 1, 1), 120);
        assertThrows(ValidationException.class, () -> controller.addFilm(film));
    }

    @Test
    @DisplayName("Ошибка при описании длиннее 200 символов")
    void shouldFailOnDescriptionOver200() {
        String longDesc = "а".repeat(201);
        Film film = makeFilm("Название", longDesc, LocalDate.of(2000, 1, 1), 120);
        assertThrows(ValidationException.class, () -> controller.addFilm(film));
    }

    @Test
    @DisplayName("Описание ровно 200 символов — допустимо")
    void shouldPassOnDescriptionExactly200() {
        String desc = "а".repeat(200);
        Film film = makeFilm("Название", desc, LocalDate.of(2000, 1, 1), 120);
        assertDoesNotThrow(() -> controller.addFilm(film));
    }

    @Test
    @DisplayName("Ошибка при дате релиза раньше 28.12.1895")
    void shouldFailOnReleaseDateBefore1895() {
        Film film = makeFilm("Название", "Описание", LocalDate.of(1895, 12, 27), 120);
        assertThrows(ValidationException.class, () -> controller.addFilm(film));
    }

    @Test
    @DisplayName("Дата релиза 28.12.1895 — допустима")
    void shouldPassOnReleaseDateExactly1895() {
        Film film = makeFilm("Название", "Описание", LocalDate.of(1895, 12, 28), 120);
        assertDoesNotThrow(() -> controller.addFilm(film));
    }

    @Test
    @DisplayName("Ошибка при нулевой продолжительности")
    void shouldFailOnZeroDuration() {
        Film film = makeFilm("Название", "Описание", LocalDate.of(2000, 1, 1), 0);
        assertThrows(ValidationException.class, () -> controller.addFilm(film));
    }

    @Test
    @DisplayName("Ошибка при отрицательной продолжительности")
    void shouldFailOnNegativeDuration() {
        Film film = makeFilm("Название", "Описание", LocalDate.of(2000, 1, 1), -1);
        assertThrows(ValidationException.class, () -> controller.addFilm(film));
    }

    private Film makeFilm(String name, String description, LocalDate releaseDate, int duration) {
        Film film = new Film();
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        return film;
    }
}