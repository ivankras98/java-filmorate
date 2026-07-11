package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;

    // Новые поля по ТЗ
    private Mpa mpa;                    // Рейтинг MPA
    private Set<Genre> genres = new HashSet<>();  // Жанры (много)

    // Оставляем для удобства в памяти (можно будет убрать позже)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Integer> likes = new HashSet<>();
}