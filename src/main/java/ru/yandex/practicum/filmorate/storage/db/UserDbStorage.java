package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.enums.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository("DBUsers")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> findAll() {
        List<User> users = jdbcTemplate.query("select * from users", (rs, rowNum) -> new User(
                rs.getInt("user_id"), rs.getString("email"),
                rs.getString("login"), rs.getString("name"),
                rs.getDate("birthday").toLocalDate()));
        for (User user : users) {
            SqlRowSet friendsRows = jdbcTemplate.queryForRowSet("select fr.friend_id, s.status from friends as fr " +
                    "join friend_status as s on fr.status_id = s.status_id where fr.user_id = ?", user.getId());
            while (friendsRows.next()) {
                user.addFriend(friendsRows.getInt("friend_id"),
                        FriendStatus.valueOf(friendsRows.getString("status")));
            }
        }
        return users;
    }

    @Override
    public Optional<User> findById(int userId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", userId);
        if (userRows.next()) {
            User user = new User(userRows.getInt("user_id"), userRows.getString("email"),
                    userRows.getString("login"), userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate());
            SqlRowSet friendsRows = jdbcTemplate.queryForRowSet("select fr.friend_id, s.status from friends as fr " +
                    "join friend_status as s on fr.status_id = s.status_id where fr.user_id = ?", userId);
            while (friendsRows.next()) {
                user.addFriend(friendsRows.getInt("friend_id"),
                        FriendStatus.valueOf(friendsRows.getString("status")));
            }
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        String sqlQuery = "insert into users(email, login, name, birthday) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public void update(User user) {
        String sqlQuery = "update users set email = ?, login = ?, name = ?, birthday = ? where user_id = ?";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        jdbcTemplate.update("delete friends where user_id = ?", user.getId());
        if (!user.getFriends().isEmpty()) {
            Map<Integer, FriendStatus> friends = user.getFriends();
            for (Integer friend : friends.keySet()) {
                jdbcTemplate.update("insert into friends(user_id, friend_id, status_id) values (?, ?, ?)",
                        user.getId(), friend, friends.get(friend).ordinal() + 1);
            }
        }
    }
}
