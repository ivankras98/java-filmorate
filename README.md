# Filmorate

Сервис для оценки фильмов и подбора рекомендаций.

## Схема базы данных

![ER-диаграмма](docs/er-diagram.png)

## Описание таблиц

- **users** — пользователи приложения
- **films** — фильмы
- **mpa_ratings** — справочник рейтингов MPA
- **genres** — справочник жанров
- **film_genres** — связь фильм-жанр
- **film_likes** — лайки
- **friendships** — дружба со статусом

## Пояснение к схеме

База данных соответствует **3NF** и поддерживает всю бизнес-логику приложения.

### Примеры SQL-запросов

**Топ-10 популярных фильмов по количеству лайков**
```sql
SELECT f.id, f.name, COUNT(fl.user_id) AS likes_count
FROM films f
LEFT JOIN film_likes fl ON f.id = fl.film_id
GROUP BY f.id, f.name
ORDER BY likes_count DESC
LIMIT 10;
```

**Список подтверждённых друзей пользователя**
```sql
SELECT u.*
FROM users u
JOIN friendships fr ON u.id = fr.friend_id
WHERE fr.user_id = 1 AND fr.status = 'CONFIRMED';
```

**Общие друзья двух пользователей**
```sql
SELECT u.*
FROM users u
JOIN friendships fr1 ON u.id = fr1.friend_id AND fr1.user_id = 1
JOIN friendships fr2 ON u.id = fr2.friend_id AND fr2.user_id = 2
WHERE fr1.status = 'CONFIRMED' AND fr2.status = 'CONFIRMED';
```

**Жанры конкретного фильма**
```sql
SELECT g.name
FROM genres g
         JOIN film_genres fg ON g.id = fg.genre_id
WHERE fg.film_id = 1;
```
