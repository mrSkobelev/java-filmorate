DROP TABLE IF EXISTS
USERS,
FILMS,
FILM_GENRE,
FRIENDS,
LIKES,
GENRES,
RATING;

CREATE TABLE IF NOT EXISTS PUBLIC.rating (
	rating_id int NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	rating_name varchar(50) NOT NULL,
	CONSTRAINT rating_pk PRIMARY KEY (rating_id)
);

CREATE TABLE IF NOT EXISTS PUBLIC.films (
	film_id int NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	film_name varchar(255) NOT NULL,
	description varchar(1000) NULL,
	release_date date NOT NULL,
	duration int NOT NULL,
	rating_id int NOT NULL,
	CONSTRAINT films_pk PRIMARY KEY (film_id),
	CONSTRAINT films_fk FOREIGN KEY (rating_id) REFERENCES public.rating(rating_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS PUBLIC.genres (
	genre_id int NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	genre_name varchar(50) NOT NULL,
	CONSTRAINT genres_pk PRIMARY KEY (genre_id)
);

CREATE TABLE IF NOT EXISTS PUBLIC.film_genre (
	film_id int NOT NULL,
	genre_id int NOT NULL,
	CONSTRAINT film_genre_fk FOREIGN KEY (film_id) REFERENCES public.films(film_id) ON DELETE CASCADE,
	CONSTRAINT film_genre_fk_1 FOREIGN KEY (genre_id) REFERENCES public.genres(genre_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS PUBLIC.users (
	user_id int NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	user_name varchar(255) NULL,
	login varchar(255) NULL,
	email varchar(255) NOT NULL,
	birthday date NOT NULL,
	CONSTRAINT users_pk PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS PUBLIC.friends (
	user_1 int NOT NULL,
	user_2 int NOT NULL,
	is_friends boolean NOT NULL DEFAULT false,
	CONSTRAINT friends_fk FOREIGN KEY (user_1) REFERENCES public.users(user_id),
	CONSTRAINT friends_fk_1 FOREIGN KEY (user_2) REFERENCES public.users(user_id)
);

CREATE TABLE IF NOT EXISTS PUBLIC.likes (
	film_id int NOT NULL,
	user_id int NOT NULL,
	CONSTRAINT likes_fk FOREIGN KEY (film_id) REFERENCES public.films(film_id),
	CONSTRAINT likes_fk_1 FOREIGN KEY (user_id) REFERENCES public.users(user_id)
);