# Filmorate

## Схема базы данных

![ER диаграмма](/Filmorate.png)

### Описание базы данных

```
Table films {
id bigint [primary key]
title varchar(127) [not null]
description varchar(255)
release_date timestamp
duration integer
rating_MPA enum
}
Table users {
id bigint [primary key]
email varchar(254) [not null]
login varchar(31) [not null]
name  varchar(31)
birthday date
}
Table genres {
id integer [primary key]
title varchar(31) [not null]
}
Table users_like_films{
user_id bigint  [primary key]
film_id bigint  [primary key]
}
Table films_genres{
film_id bigint  [primary key]
genre_id integer  [primary key]
}
Table friendship{
user_who_send_id bigint  [primary key]
user_must_confirm_id bigint  [primary key]
status_friendship enum
}
Ref: users_like_films.user_id > users.id
Ref: users_like_films.film_id > films.id
Ref: films_genres.film_id > films.id
Ref: films_genres.genre_id > genres.id
Ref: friendship.user_who_send_id > users.id
Ref: friendship.user_must_confirm_id > users.id
```

## Примеры SQL запросов к базе данных

**Добавить фильм**

```
insert into films("title", "descriptoin","duration", "rating_mpa", "release_date")
 VALUES('Гарри Поттер', 'и филосовский камень','90' ,'PG','2005-05-05');
 ```

**Обновить фильм**

```
update films set duration = 120
where id = 5;
```

**Удалить фильм**

```
delete from films
where id = 5;
```

**Получить названия всех фильмов**

```
select title
from films
order by title;
```

**Получить фильм по id**

```
select *
from films
where id = 1;
```

**Получить 10 самых популярных фильмов**

```
select f.title
from films f join users_like_films ulf on f.id =ulf .film_id
group by f.title
order by count(user_id) desc
limit 10;
```

**Добавить лайк фильму**

```
insert into users_like_films ("user_id", "film_id")
values ('1', '1');
```

**Удалить лайк**

```
delete from users_like_films
where user_id = 1
and film_id = 1;
```

**Добавить в друзья**

```
insert into friendship ("user_who_send_id", "user_must_confirm_id", "status")
values('1', '3', 'not_approved');
```

**Удалить из друзей**

```
delete from friendship
where  user_who_send_id in (1, 3) and user_must_confirm_id in(1, 3);
```

**Получить список всех друзей пользователя**

```
select u."name"
from friendship f
left join users u on ( f.user_who_send_id = u.id  OR f.user_must_confirm_id=u.id)
where (f.user_who_send_id  =4 or f.user_must_confirm_id =4) and  f.status='approved' and u.id !=4 ;
```

**Получить список общих друзей**

```
select u.name
from users u
where u.id in
(select f1.user_must_confirm_id
from friendship f1
where f1.user_who_send_id in(3, 4) and f1.status = 'approved'
group by f1.user_must_confirm_id
having f1.user_must_confirm_id in (select f2.user_who_send_id
from friendship f2
where f2.user_must_confirm_id in(3, 4) and f2.status = 'approved'));
```