DROP TABLE IF EXISTS film CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS ratings_mpa CASCADE;
DROP TABLE IF EXISTS film_likes CASCADE;
DROP TABLE IF EXISTS friends CASCADE;
DROP TABLE IF EXISTS film_genres CASCADE;
DROP TABLE IF EXISTS genres CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS reviews_dislikes CASCADE;
DROP TABLE IF EXISTS reviews_likes CASCADE;
DROP TABLE IF EXISTS film_directors CASCADE;
DROP TABLE IF EXISTS directors CASCADE;
DROP TABLE IF EXISTS events CASCADE;

CREATE TABLE IF NOT EXISTS films
(
    film_id      INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name         VARCHAR(255) NOT NULL,
    description  TEXT(200),
    release_date DATE,
    duration     INT,
    rating_id    INT
);

CREATE TABLE IF NOT EXISTS users
(
    user_id  INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
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
    like_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    film_id INT,
    user_id INT
);

CREATE TABLE IF NOT EXISTS friends
(
    friends_id       INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id          INT,
    friend_id        INT,
    is_friend_status BOOLEAN
);

CREATE TABLE IF NOT EXISTS film_genres
(
    film_genres_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    film_id        INT,
    genre_id       INT
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id INT          NOT NULL PRIMARY KEY,
    name     VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS reviews
(
    review_id   INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    content     VARCHAR(255),
    is_positive BOOL,
    user_id     INT,
    film_id     INT,
    useful      INT
);

CREATE TABLE IF NOT EXISTS reviews_likes
(
    reviews_like_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    review_id       INT,
    user_id         INT
);

CREATE TABLE IF NOT EXISTS reviews_dislikes
(
    reviews_dislike_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    review_id          INT,
    user_id            INT
);

CREATE TABLE IF NOT EXISTS film_directors
(
    film_director_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    film_id          INT,
    director_id      INT
);

CREATE TABLE IF NOT EXISTS directors
(
    director_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS events
(
    event_id        INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    event_type      VARCHAR(255) NOT NULL,
    event_operation VARCHAR(255) NOT NULL,
    user_id         INT          NOT NULL,
    entity_id       INT          NOT NULL,
    event_timestamp TIMESTAMP    NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);
