package ru.filmorate.storage;

import ru.filmorate.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The interface User storage
 */

public interface UserStorage {

    /**
     * Find all list
     * @return the list of users
     */

    List<User> findAll();

    /**
     * Find by id
     * @param userId the user id
     * @return the optional of users
     */

    Optional<User> findById(int userId);

    /**
     * Create user
     * @param user the user
     * @return the user
     */

    User create(User user);

    /**
     * Update
     * @param user the user
     */

    void update(User user);

    /**
     * Find users liked films
     * @return the list of users
     */

    List<Map<String, Object>> findUsersLikedFilms();

    /**
     * Find users liked films by id
     * @param id the id
     * @return the list of users
     */

    List<Map<String, Object>> findUsersLikedFilmsById(int id);

    /**
     * Find users liked films for top films
     * @param count the count
     * @return the list of users
     */

    List<Map<String, Object>> findUsersLikedFilmsForTopFilms(int count);

    /**
     * Delete users liked films
     * @param filmId the film id
     */

    void deleteUsersLikedFilms(int filmId);

    /**
     * Add users liked films
     * @param filmId the film id
     * @param userId the user id
     */

    void addUsersLikedFilms(int filmId, int userId);
}