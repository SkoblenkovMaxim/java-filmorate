package ru.yandex.practicum.filmorate.storage.user;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.user.User;

@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    @SuppressWarnings("all")
    private static final String CREATE_QUERY = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";

    @SuppressWarnings("all")
    private static final String GET_ALL_QUERY = "SELECT * FROM users";

    @SuppressWarnings("all")
    private static final String GET_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";

    @SuppressWarnings("all")
    private static final String DELETE_USER_BY_ID_QUERY = "DELETE FROM users WHERE user_id = ?";

    @SuppressWarnings("all")
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_QUERY, new String[]{"user_id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        var key = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return User.builder()
                .id(key)
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .build();
    }

    @Override
    public void removeUser(Long userId) {
        if (getUserById(userId) == null) {
            throw new NotFoundException("User " + userId + " is not found");
        }
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(DELETE_USER_BY_ID_QUERY);
            ps.setLong(1, userId);
            return ps;
        });
    }

    @Override
    public User updateUser(User user) {
        jdbcTemplate.update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId()
        );
        return User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .build();
    }

    @Override
    public Collection<User> getUsers() {
        return jdbcTemplate.query(
                GET_ALL_QUERY,
                UserDbStorage::resultSetToUser
        );
    }

    @Override
    public User getUserById(Long userId) {
        return jdbcTemplate.query(
                GET_BY_ID_QUERY,
                rs -> rs.next() ? resultSetToUser(rs, 1) : null,
                userId
        );
    }

    private static User resultSetToUser(ResultSet rs, int i) throws SQLException {
        return User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

    @Override
    public boolean isContains(Long id) {
        log.debug("isContains({})", id);
        try {
            getUserById(id);
            log.trace("The user with id {} was found", id);
            return true;
        } catch (EmptyResultDataAccessException exception) {
            log.trace("No information was found for user with id {}", id);
            return false;
        }
    }
}
