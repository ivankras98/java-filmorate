# Filmorate

Сервис для оценки фильмов и подбора рекомендаций.

## Схема базы данных

![ER-диаграмма](docs/er-diagram.png)

## Описание таблиц

- users — пользователи приложения
- films — фильмы с описанием и рейтингом MPA
- mpa_ratings — справочник рейтингов MPA (G, PG, PG-13, R, NC-17)
- genres — справочник жанров (Комедия, Драма, Мультфильм, Триллер, Документальный, Боевик)
- film_genres — связь фильмов и жанров (многие-ко-многим), составной PK (film_id, genre_id)
- film_likes — лайки пользователей фильмам, составной PK (film_id, user_id)
- friendships — дружба между пользователями, составной PK (user_id, friend_id), статус: CONFIRMED/UNCONFIRMED

## Примеры SQL-запросов

### Получить все фильмы с рейтингом MPA
SELECT f.*, m.name AS mpa_rating
FROM films f
JOIN mpa_ratings m ON f.mpa_rating_id = m.id;

### Получить фильм с рейтингом MPA и жанрами
SELECT f.*, m.name AS mpa_rating, g.name AS genre
FROM films f
JOIN mpa_ratings m ON f.mpa_rating_id = m.id
LEFT JOIN film_genres fg ON f.id = fg.film_id
LEFT JOIN genres g ON fg.genre_id = g.id
WHERE f.id = 1;

### Получить всех пользователей
SELECT * FROM users;

### Получить топ-10 популярных фильмов по лайкам
SELECT f.*, COUNT(fl.user_id) AS likes_count
FROM films f
LEFT JOIN film_likes fl ON f.id = fl.film_id
GROUP BY f.id
ORDER BY likes_count DESC
LIMIT 10;

### Поставить лайк фильму
INSERT INTO film_likes (film_id, user_id) VALUES (1, 2);

### Удалить лайк
DELETE FROM film_likes WHERE film_id = 1 AND user_id = 2;

### Добавить в друзья (неподтверждённая дружба)
INSERT INTO friendships (user_id, friend_id, status)
VALUES (1, 2, 'UNCONFIRMED');

### Подтвердить дружбу (обновляем исходную заявку)
UPDATE friendships SET status = 'CONFIRMED'
WHERE user_id = 1 AND friend_id = 2;

### Удалить из друзей
DELETE FROM friendships
WHERE user_id = 1 AND friend_id = 2;

### Получить список друзей пользователя
SELECT u.*
FROM users u
JOIN friendships fr ON u.id = fr.friend_id
WHERE fr.user_id = 1
AND fr.status = 'CONFIRMED';

### Получить общих друзей двух пользователей
SELECT u.*
FROM users u
JOIN friendships fr1 ON u.id = fr1.friend_id AND fr1.user_id = 1
JOIN friendships fr2 ON u.id = fr2.friend_id AND fr2.user_id = 2
WHERE fr1.status = 'CONFIRMED'
AND fr2.status = 'CONFIRMED';

### Получить все жанры фильма
SELECT g.name
FROM genres g
JOIN film_genres fg ON g.id = fg.genre_id
WHERE fg.film_id = 1;