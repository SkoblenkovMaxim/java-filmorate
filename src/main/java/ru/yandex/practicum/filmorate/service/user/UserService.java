package ru.yandex.practicum.filmorate.service.user;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.director.Director;
import ru.yandex.practicum.filmorate.model.event.EventOperation;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.FilmDto;
import ru.yandex.practicum.filmorate.model.film.FilmMapper;
import ru.yandex.practicum.filmorate.model.friend.Friends;
import ru.yandex.practicum.filmorate.model.genre.Genre;
import ru.yandex.practicum.filmorate.model.rating.Rating;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.UserDto;
import ru.yandex.practicum.filmorate.model.user.UserMapper;
import ru.yandex.practicum.filmorate.service.event.EventService;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.raiting.RatingStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import static java.lang.String.format;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendStorage friendStorage;
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final GenreStorage genreStorage;
    private final RatingStorage ratingStorage;
    private final DirectorStorage directorStorage;
    private final UserMapper userMapper;
    private final FilmMapper filmMapper;
    private final EventService eventService;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("likeDbStorage") LikeStorage likeStorage,
                       @Qualifier("genreDbStorage") GenreStorage genreStorage,
                       @Qualifier("ratingDbStorage") RatingStorage ratingStorage,
                       @Qualifier("directorDbStorage") DirectorStorage directorStorage,
                       FriendStorage friendStorage,
                       UserMapper userMapper,
                       FilmMapper filmMapper, EventService eventService) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
        this.friendStorage = friendStorage;
        this.genreStorage = genreStorage;
        this.ratingStorage = ratingStorage;
        this.directorStorage = directorStorage;
        this.userMapper = userMapper;
        this.filmMapper = filmMapper;
        this.eventService = eventService;
    }

    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        user.setName(getDisplayedName(user));
        return userMapper.toUserDto(userStorage.createUser(user));
    }

    public UserDto updateUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);

        User userFromDb = userStorage.getUserById(user.getId());
        if (userFromDb != null) {
            return userMapper.toUserDto(userStorage.updateUser(user));
        } else {
            throw new NotFoundException("id " + user.getId() + " не найден");
        }
    }

    public Collection<UserDto> getUsers() {
        return userStorage.getUsers().stream()
                .map(userMapper::toUserDto)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public UserDto getUserById(Long userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("User " + userId + " is not found");
        }
        return userMapper.toUserDto(user);
    }

    public void removeUser(Long userId) {
        userStorage.removeUser(userId);
    }

    // добавление в друзья
    public void addFriends(Long userId, Long friendId) {
        checkIfFriend(userId, friendId);
        boolean isFriendStatus = friendStorage.isFriendStatus(userId, friendId);
        friendStorage.addFriend(userId, friendId, isFriendStatus);
        eventService.createFriendEvent(userId, friendId, EventOperation.ADD);
    }

    // удаление из друзей
    public void deleteFriend(Long userId, Long friendId) {
        checkIfNotFriend(userId, friendId);
        if (friendStorage.isFriendStatus(userId, friendId)
                || !getUsers().contains(getUserById(friendId))) {
            friendStorage.deleteFriend(userId, friendId);
        }
        eventService.createFriendEvent(userId, friendId, EventOperation.REMOVE);
    }

    // вывод списка друзей
    public List<Long> getFriends(Long userId) {

        if (!getUsers().contains(getUserById(userId))) {
            throw new NotFoundException(format("Пользователь с id= %d не найден", userId));
        }

        List<Long> friends = friendStorage.getFriends(userId);
        log.trace("The user's friends list were returned: {}", friends);
        return friends;
    }

    public List<UserDto> getFriendsByUserId(Long userId) {
        List<Friends> friends = friendStorage.getFriendsByUserId(userId);

        if (!getUsers().contains(getUserById(userId))) {
            throw new NotFoundException(format("Пользователь с id= %d не найден", userId));
        }

        List<User> usersFromDb = new ArrayList<>();
        friends.forEach(friend -> {
            User user = userStorage.getUserById(friend.getFriendId());
            usersFromDb.add(user);
        });

        log.trace("The user's friends list were returned: {}", friends);
        return usersFromDb.stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    // вывод списка общих друзей
    public List<UserDto> getCommonFriends(Long userId, Long friendId) {
        log.info("Получение списка общих друзей пользователей {} и {}", userId, friendId);

        List<UserDto> userFriends = getFriendsByUserId(userId);
        List<UserDto> friendFriends = getFriendsByUserId(friendId);
        List<UserDto> commonFriends = new ArrayList<>();

        if (isValidUser(userId) || isValidUser(friendId)) {
            userFriends.forEach(userFriend -> {
                if (friendFriends.contains(userFriend)) {
                    commonFriends.add(userFriend);
                }
            });
            return commonFriends;
        }
        log.debug("Пользователь с id={} не найден", userId);
        throw new NotFoundException("Пользователь с id=" + userId + " не найден");
    }

    public List<FilmDto> getUsersRecommendations(Long userId) {
        if (!isValidUser(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        List<Long> recommendUserFilms = filmStorage.getUsersRecommendations(userId);
        log.info("Список фильмов пересечений");
        List<Long> userFilms = filmStorage.getFilmsLikesByUser(userId);
        log.info("Список фильмов, которые лайкнул пользователь {}", userId);
        recommendUserFilms.removeAll(userFilms);
        List<Film> films = recommendUserFilms
                .stream()
                .map(filmStorage::getFilm)
                .toList();
        films.forEach(this::fillFilmAdditionalInfo);
        return films.stream()
                .map(filmMapper::toFilmDto)
                .collect(Collectors.toList());
    }

    //Проверка наличия пользователя в хранилище
    public boolean isValidUser(Long userId) {
        return userStorage.getUserById(userId) != null;
    }

    private void checkIfFriend(Long userId, Long friendId) {
        log.debug("checkIfFriend({}, {})", userId, friendId);
        if (userId.equals(friendId)) {
            throw new NotFoundException("Attempt to add yourself into a friends list, the id is " + userId);
        }
        if (!getUsers().contains(getUserById(userId))) {
            throw new NotFoundException(format("User with id %d wasn't found", userId));
        }
        if (!getUsers().contains(getUserById(friendId))) {
            throw new NotFoundException(format("User with id %d wasn't found", userId));
        }
        if (friendStorage.isFriendStatus(userId, friendId)) {
            throw new RuntimeException(
                    format("The user with id %d is already friend of user with id %d", userId, friendId));
        }
    }

    private void checkIfNotFriend(Long userId, Long friendId) {
        log.debug("checkIfNotFriend({}, {})", userId, friendId);
        if (userId.equals(friendId)) {
            throw new NotFoundException(
                    "Attempt to delete yourself from a friends list, the id is " + userId);
        }
        if (!getUsers().contains(getUserById(userId))) {
            throw new NotFoundException(format("User with id %d wasn't found", userId));
        }
        if (!getUsers().contains(getUserById(friendId))) {
            throw new NotFoundException(format("User with id %d wasn't found", friendId));
        }
    }
    private String getDisplayedName(User user) {
        return user.getName() != null && !user.getName().isBlank() ? user.getName() : user.getLogin();
    }

    private void fillFilmAdditionalInfo(Film film) {
        Rating rating = ratingStorage.getRatingById(film.getMpa().getId());
        if (rating != null) {
            film.getMpa().setName(rating.getName());
            film.getMpa().setDescription(rating.getDescription());
        }

        List<Genre> genreList = new ArrayList<>();
        genreStorage.getFilmGenresByFilmId(film.getId())
                .forEach(filmGenre -> {
                    Genre genre = genreStorage.getGenreById(filmGenre.getGenreId());
                    if (genre != null) {
                        genreList.add(genre);
                    }
                });
        genreList.sort(Comparator.comparing(Genre::getId));
        film.setGenres(new HashSet<>(genreList).stream().toList());

        List<Director> directors = directorStorage.getDirectorsByFilmId(film.getId());
        film.setDirectors(directors);

        film.setLikeCount(likeStorage.getLikesCount(film.getId()));
    }
}
