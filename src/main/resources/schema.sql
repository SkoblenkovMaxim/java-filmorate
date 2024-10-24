CREATE TABLE IF NOT EXISTS films
(
    film_id      INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    description  TEXT(200),
    release_date DATE,
    duration     INT,
    rating_id    INT
);

CREATE TABLE IF NOT EXISTS users
(
    user_id  INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    email    VARCHAR(255),
    login    VARCHAR(255),
    name     VARCHAR(255),
    birthday DATE
);

CREATE TABLE IF NOT EXISTS ratings_mpa
(
    rating_id   INT          NOT NULL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS film_likes
(
    like_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    film_id INT,
    user_id INT
);

CREATE TABLE IF NOT EXISTS friends
(
    friends_id       INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id          INT,
    friend_id        INT,
    is_friend_status BOOLEAN
);

CREATE TABLE IF NOT EXISTS film_genres
(
    film_genres_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    film_id        INT,
    genre_id       INT
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id INT          NOT NULL PRIMARY KEY,
    name     VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS directors
(
    director_id INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_directors
(
    film_director_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    film_id          INT,
    director_id      INT
);

