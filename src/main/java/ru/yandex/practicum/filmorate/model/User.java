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

    // Друзья (для удобства работы в памяти)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Integer> friends = new HashSet<>();

    // Можно добавить (по желанию), чтобы хранить статусы:
    // private Set<Friendship> friendships = new HashSet<>();
}