![ALT-image](https://github.com/mrSkobelev/java-filmorate/blob/update_readme/schema.png)


# Примеры запросов к БД 

## -- выбрать топ 3 популярных фильма
````
select f.film_name 
from films f 
join likes l on f.film_id = l.film_id
group by f.film_name
order by count(l.film_id) desc
limit 3;
````

## -- найти все фильмы в жанре комедии
````
select f.film_name 
from films f 
join "film-genre" fg on f.film_id = fg.film_id
join genres g on fg.genre_id = g.genre_id 
where g.genre_name = 'Комедия'
group by f.film_name;
````

## -- найти фильмы без описаний
````
select f.film_name 
from films f 
where f.description is null;
````

## -- узнать какие фильмы нравятся юзеру c id=1
````
select f.film_name 
from users u
join likes l on u.user_id = l.user_id 
join films f on l.film_id = f.film_id
where u.user_id = 1;
````

## -- узнать дружат ли пользователи с id=1 и id=2
````
select f.is_friends
from friends f 
where f.user_1 = 1 and f.user_2 = 2;
````

## -- какой жанр больше всего предпочитает пользователь с id=1
````
select g.genre_name 
from genres g 
join "film-genre" fg on g.genre_id = fg.genre_id 
join films f on fg.film_id = f.film_id 
join likes l on f.film_id = l.film_id 
join users u on l.user_id = u.user_id 
where u.user_id = 1
group by g.genre_name
order by COUNT(g.genre_name) desc
limit 1;
````
