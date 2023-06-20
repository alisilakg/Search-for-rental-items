package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    List<User> getListAllUsers();

    User getUserById(Long id);

    User updateUser(User user);

    void deleteUserById(Long id);
}
