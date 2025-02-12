package ru.yandex.practicum.filmorate.storage.friend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.friend.Friends;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendDbStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;

    @SuppressWarnings("all")
    @Override
    public void addFriend(Long userId, Long friendId, boolean isFriendStatus) {

        if (!getFriends(userId).contains(friendId) && !getFriends(friendId).contains(userId)) {
            jdbcTemplate.update(connection -> {
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO friends (user_id, friend_id, is_friend_status) VALUES (?, ?, ?)",
                        new String[]{"friends_id"}
                );
                statement.setLong(1, userId);
                statement.setLong(2, friendId);
                statement.setBoolean(3, isFriendStatus);
                log.info("Added friend: {}", friendId);
                return statement;
            });
        }

        if (isFriendStatus(userId, friendId)) {
            jdbcTemplate.update(connection -> {
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE friends SET is_friend_status=true WHERE user_id=? AND friend_id=?");
                statement.setLong(1, userId);
                statement.setLong(2, friendId);
                return statement;
            });
        }
    }

    @SuppressWarnings("all")
    @Override
    public void deleteFriend(Long userId, Long friendId) {
        Friends friends = Objects.requireNonNull(getFriend(userId, friendId));
        jdbcTemplate.update("DELETE FROM friends WHERE user_id=? AND friend_id=?", userId, friendId);
        if (friends.isFriendStatus()) {
            jdbcTemplate.update("UPDATE friends SET is_friend_status=false WHERE user_id=? AND friend_id=?",
                    userId, friendId);
            log.debug("The friendship between {} and {} is over", userId, friendId);
        }
        log.info("Не найден friend: {}", friends);
    }

    @SuppressWarnings("all")
    @Override
    public List<Long> getFriends(Long userId) {
        return jdbcTemplate.query(
                        "SELECT user_id, friend_id, is_friend_status FROM friends WHERE user_id=?",
                        FriendDbStorage::mapRow,
                        userId)
                .stream()
                .map(Friends::getFriendId)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("all")
    @Override
    public List<Friends> getFriendsByUserId(Long userId) {
        return jdbcTemplate.query(
                "SELECT * FROM friends WHERE user_id=?",
                FriendDbStorage::mapRow,
                userId
        );
    }

    @SuppressWarnings("all")
    @Override
    public Friends getFriend(Long userId, Long friendId) {
        return jdbcTemplate.queryForObject(
                "SELECT user_id, friend_id, is_friend_status FROM friends WHERE user_id=? AND friend_id=?",
                FriendDbStorage::mapRow,
                userId, friendId);
    }

    @Override
    public boolean isFriendStatus(Long userId, Long friendId) {
        log.debug("isFriend({}, {})", userId, friendId);
        try {
            getFriend(userId, friendId);
            log.trace("Found friendship between {} and {}", userId, friendId);
            return true;
        } catch (EmptyResultDataAccessException exception) {
            log.trace("No friendship were found between {} and {}", userId, friendId);
            return false;
        }
    }

    public static Friends mapRow(ResultSet rs, int rowNum) throws SQLException {
        Friends friend = new Friends();
        friend.setUserId(rs.getLong("user_id"));
        friend.setFriendId(rs.getLong("friend_id"));
        friend.setFriendStatus(rs.getBoolean("is_friend_status"));
        return friend;
    }
}
