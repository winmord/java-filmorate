# java-filmorate

![alt text](https://github.com/winmord/java-filmorate/blob/main/repo_scheme.png)

### Основные операции:

- Получить всех пользователей
  ```sql
    SELECT *
    FROM user
    WHERE user.deleted_at IS NULL;
  ```
- Получить пользователя по id
  ```sql
    SELECT *
    FROM user
    WHERE user.user_id = target_id
      AND user.deleted_at IS NULL;
  ```
- Получить всех друзей пользователя (подтверждённых и неподтверждённых)
  ```sql
    SELECT *
    FROM user
    WHERE user.user_id IN (SELECT friend_id
                           FROM friendship
                           WHERE friendship.user_id = target_id
                             AND friendship.deleted_at IS NULL)
      AND user.deleted_at IS NULL;
  ```
- Получить только подтверждённых друзей пользователя
  ```sql
    SELECT *
    FROM user
    WHERE user.user_id IN (SELECT friend_id
                           FROM friendship
                           WHERE friendship.user_id = target_id
                             AND friendship.confirmed_at IS NOT NULL
                             AND friendship.deleted_at IS NULL)
      AND user.deleted_at IS NULL;
  ```
- Получить все фильмы
  ```sql
    SELECT *
    FROM film
    WHERE film.deleted_at IS NULL;
  ```
- Получить фильм по id
  ```sql
    SELECT *
    FROM film
    WHERE film.film_id = target_id
      AND user.deleted_at IS NULL;
  ```
- Получить Топ-10 фильмов по количеству лайков
  ```sql
    SELECT *
    FROM film
        LEFT JOIN (SELECT film_id, count(film_like.film_id) AS count
          FROM film_like
          WHERE film_like.deleted_at IS NULL
          GROUP BY film_like.film_id) AS top ON top.film_id = film.film_id
             INNER JOIN mpa_rating ON film.mpa_rating_id = mpa_rating.mpa_rating_id
    WHERE film.deleted_at IS NULL
    ORDER BY count DESC
    LIMIT 10;
  ```
  
  


### Ревью дизайна БД:

Декомпозиция таблиц выполнена правильно. 
Ключи во всех таблицах определены правильно.
Отношения между таблицами выстроены тоже верно.

Вижу некоторые моменты, на которые хочу обратить внимание:
1. :white_check_mark: Возможно появление потенциальных проблем в таблицах **film_like** и **friendship**: уже достоверно известно, что по ТЗ мы можем удалять лайки и отменять дружбу. В такой архитектуре БД придется удалять записи, а значит мы будем безвозвратно терять информацию о том, что лайк или дружба ранее существовали. Это может негативно сказаться на отчетности за прошлые периоды. Возможно, будущие ТЗ четко определят, что нам эта информация будет не нужна, но я все-таки бы посоветовал добавить к этим таблицам некоторый признак удаления записи (дату или булево).
2. :white_check_mark: Так же, как в предыдущем пункте, скорее всего для таблиц **user** и **film** также следует добавить пометку удаления, хотя сейчас неизвестно, будем ли мы по ТЗ удалять фильмы или юзеров, скорее всего это пригодится.
3. :white_check_mark: В таблицу **mpa_rating** я бы добавил описание рейтинга, так как оно приведено в ТЗ и может пригодиться далее.
4. :white_check_mark: Скорее всего стоит описать логику заведения дружбы, ее "жизненный цикл", описать возможные статусы и под них сделать запросы. Сейчас запрос получения друзей юзера есть, но в нем не учитывается статус дружбы - подтвержденная/не подтвержденная.

В целом работа отличная, такая структура и теоретически и практически выполнена правильно, покрывает всю необходимую логику и имеет потенциал к расширению и доработкам.
У меня получилась схема как брат-близнец твоей, так что мне все нравится)
