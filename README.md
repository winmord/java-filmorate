# java-filmorate

![alt text](https://github.com/winmord/java-filmorate/blob/main/repo_scheme.png)

### Основные операции:

- Получить всех пользователей
  ```sql
    SELECT *
    FROM user;
  ```
- Получить пользователя по id
  ```sql
    SELECT *
    FROM user
    WHERE user.user_id = target_id;
  ```
- Получить друзей пользователя
  ```sql
    SELECT *
    FROM user
    WHERE user.user_id IN (SELECT friend_id
                           FROM friendship
                           WHERE friendship.user_id = target_id);
  ```
- Получить все фильмы
  ```sql
    SELECT *
    FROM film;
  ```
- Получить фильм по id
  ```sql
    SELECT *
    FROM film
    WHERE film.film_id = target_id;
  ```
- Получить Топ-10 фильмов по количеству лайков
  ```sql
    SELECT film.*
    FROM (SELECT film_like.film_id
          FROM film_like
          GROUP BY film_like.film_id
          ORDER BY count(film_like.film_id) DESC
          LIMIT 10) AS top
    INNER JOIN film ON top.film_id = film.film_id;
  ```