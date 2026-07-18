package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Integer id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    // Хранит id друзей для текущей реализации в памяти.
    // При переходе на БД будет заменено на работу с таблицей friendships,
    // которая поддерживает статусы CONFIRMED/UNCONFIRMED.
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Integer> friends = new HashSet<>();
}