package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationTest {

    private UserController controller;

    @BeforeEach
    void setUp() {
        controller = new UserController();
    }

    @Test
    void shouldCreateValidUser() {
        User user = makeUser("test@mail.ru", "login", "Имя", LocalDate.of(1990, 1, 1));
        User result = controller.createUser(user);
        assertNotNull(result.getId());
        assertEquals("Имя", result.getName());
    }

    @Test
    void shouldUseLoginAsNameWhenNameIsEmpty() {
        User user = makeUser("test@mail.ru", "login", "", LocalDate.of(1990, 1, 1));
        User result = controller.createUser(user);
        assertEquals("login", result.getName());
    }

    @Test
    void shouldFailOnEmptyEmail() {
        User user = makeUser("", "login", "Имя", LocalDate.of(1990, 1, 1));
        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    void shouldFailOnEmailWithoutAt() {
        User user = makeUser("wrongemail.ru", "login", "Имя", LocalDate.of(1990, 1, 1));
        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    void shouldFailOnEmptyLogin() {
        User user = makeUser("test@mail.ru", "", "Имя", LocalDate.of(1990, 1, 1));
        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    void shouldFailOnLoginWithSpaces() {
        User user = makeUser("test@mail.ru", "log in", "Имя", LocalDate.of(1990, 1, 1));
        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    void shouldFailOnFutureBirthday() {
        User user = makeUser("test@mail.ru", "login", "Имя", LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    void shouldPassOnTodayBirthday() {
        User user = makeUser("test@mail.ru", "login", "Имя", LocalDate.now());
        assertDoesNotThrow(() -> controller.createUser(user));
    }

    private User makeUser(String email, String login, String name, LocalDate birthday) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(birthday);
        return user;
    }
}