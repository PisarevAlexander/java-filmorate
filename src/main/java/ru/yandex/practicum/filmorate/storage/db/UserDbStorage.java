package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository("DBUsers")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> findAll() {
        List<User> users = jdbcTemplate.query("SELECT * FROM users", (rs, rowNum) -> makeUser(rs));
        for (User user : users) {
            SqlRowSet friendsRows = jdbcTemplate.queryForRowSet("SELECT fr.friend_id, s.status " +
                    "FROM friends AS fr " +
                    "JOIN friend_status AS s ON fr.status_id = s.status_id " +
                    "WHERE fr.user_id = ?", user.getId());
            while (friendsRows.next()) {
                user.addFriend(friendsRows.getInt("friend_id"),
                        FriendStatus.valueOf(friendsRows.getString("status")));
            }
        }
        return users;
    }

    @Override
    public Optional<User> findById(int userId) {
        try {
            User user = jdbcTemplate.queryForObject("SELECT * FROM users WHERE user_id = ?",
                    ((rs, rowNum) -> makeUser(rs)), userId);
            SqlRowSet friendsRows = jdbcTemplate.queryForRowSet("SELECT fr.friend_id, s.status " +
                    "FROM friends as fr " +
                    "JOIN friend_status AS s ON fr.status_id = s.status_id " +
                    "WHERE fr.user_id = ?", userId);
            while (friendsRows.next()) {
                user.addFriend(friendsRows.getInt("friend_id"),
                        FriendStatus.valueOf(friendsRows.getString("status")));
            }
            return Optional.of(user);
        } catch (DataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        String sqlQuery = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)";
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
        String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        jdbcTemplate.update("DELETE friends WHERE user_id = ?", user.getId());
        if (!user.getFriends().isEmpty()) {
            Map<Integer, FriendStatus> friends = user.getFriends();
            for (Integer friend : friends.keySet()) {
                jdbcTemplate.update("INSERT INTO friends(user_id, friend_id, status_id) VALUES (?, ?, ?)",
                        user.getId(), friend, friends.get(friend).ordinal() + 1);
            }
        }
    }

    @Override
    public List<Map<String, Object>> findUsersLikedFilms() {
        return jdbcTemplate.queryForList("SELECT * FROM users_liked_films");
    }

    @Override
    public List<Map<String, Object>> findUsersLikedFilmsById(int id) {
        return jdbcTemplate.queryForList("SELECT * " +
                "FROM users_liked_films " +
                "WHERE film_id = ?", id);
    }

    @Override
    public List<Map<String, Object>> findUsersLikedFilmsForTopFilms(int count) {
        return jdbcTemplate.queryForList("SELECT * " +
                "FROM users_liked_films " +
                "WHERE film_id IN (SELECT f.film_id " +
                "FROM films AS f " +
                "LEFT JOIN mpa_rating AS r ON f.rating_id = r.rating_id " +
                "LEFT JOIN users_liked_films AS l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY sum(l.user_id) DESC " +
                "LIMIT ?)", count);
    }

    @Override
    public void deleteUsersLikedFilms(int filmId) {
        jdbcTemplate.update("DELETE users_liked_films WHERE film_id = ?", filmId);
    }

    @Override
    public void addUsersLikedFilms(int filmId, int userId) {
        jdbcTemplate.update("INSERT INTO users_liked_films(film_id, user_id) VALUES (?, ?)",
                filmId, userId);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        return new User(rs.getInt("user_id"), rs.getString("email"), rs.getString("login"),
                rs.getString("name"), rs.getDate("birthday").toLocalDate());
    }
}
