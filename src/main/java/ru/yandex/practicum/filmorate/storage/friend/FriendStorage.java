package ru.yandex.practicum.filmorate.storage.friend;

import ru.yandex.practicum.filmorate.model.friend.Friends;

import java.util.List;

public interface FriendStorage {

    void addFriend(Long userId, Long friendId, boolean isFriend);

    void deleteFriend(Long userId, Long friendId);

    List<Long> getFriends(Long userId);

    Friends getFriend(Long userId, Long friendId);

    boolean isFriendStatus(Long userId, Long friendId);
}
