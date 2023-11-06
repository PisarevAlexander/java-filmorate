package ru.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;
import ru.filmorate.model.enums.FriendStatus;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * The type User
 */

@Data
public class User {
    private int id;
    private String email;
    private String login;
    private String name;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private Map<Integer, FriendStatus> friends;

    /**
     * Instantiates a new User
     * @param id       the id
     * @param email    the email
     * @param login    the login
     * @param name     the name
     * @param birthday the birthday
     */

    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        friends =  new HashMap();
    }

    /**
     * Delete friend id
     * @param friendId the friend id
     */

    public void deleteFriendId(int friendId) {
        friends.remove(friendId);
    }

    /**
     * Add friend
     * @param friendId     the friend id
     * @param friendStatus the friend status
     */

    public void addFriend(int friendId, FriendStatus friendStatus) {
        friends.put(friendId, friendStatus);
    }
}