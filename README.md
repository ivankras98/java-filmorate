# \# Filmorate

# 

# Сервис для оценки фильмов и подбора рекомендаций.

# 

# \## Схема базы данных

# 

# !\[ER-диаграмма](docs/er-diagram.png)

# 

# \## Описание таблиц

# 

# \- \*\*users\*\* — пользователи приложения

# \- \*\*films\*\* — фильмы с описанием и рейтингом MPA

# \- \*\*mpa\_ratings\*\* — справочник рейтингов MPA (G, PG, PG-13, R, NC-17)

# \- \*\*genres\*\* — справочник жанров (Комедия, Драма и т.д.)

# \- \*\*film\_genres\*\* — связь фильмов и жанров (многие-ко-многим)

# \- \*\*film\_likes\*\* — лайки пользователей фильмам

# \- \*\*friendships\*\* — дружба между пользователями со статусом (CONFIRMED/UNCONFIRMED)

# 

# \## Примеры SQL-запросов

# 

# \### Получить все фильмы

# SELECT f.\*, m.name AS mpa\_rating

# FROM films f

# JOIN mpa\_ratings m ON f.mpa\_rating\_id = m.id;

# 

# \### Получить топ-10 популярных фильмов по лайкам

# SELECT f.\*, COUNT(fl.user\_id) AS likes\_count

# FROM films f

# LEFT JOIN film\_likes fl ON f.id = fl.film\_id

# GROUP BY f.id

# ORDER BY likes\_count DESC

# LIMIT 10;

# 

# \### Получить список друзей пользователя

# SELECT u.\*

# FROM users u

# JOIN friendships fr ON u.id = fr.friend\_id

# WHERE fr.user\_id = 1

# AND fr.status = 'CONFIRMED';

# 

# \### Получить общих друзей двух пользователей

# SELECT u.\*

# FROM users u

# JOIN friendships fr1 ON u.id = fr1.friend\_id AND fr1.user\_id = 1

# JOIN friendships fr2 ON u.id = fr2.friend\_id AND fr2.user\_id = 2

# WHERE fr1.status = 'CONFIRMED'

# AND fr2.status = 'CONFIRMED';

# 

# \### Получить все жанры фильма

# SELECT g.name

# FROM genres g

# JOIN film\_genres fg ON g.id = fg.genre\_id

# WHERE fg.film\_id = 1;

