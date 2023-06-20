package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Repository
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long counterIdUser = 1L;

    public Long generateIdUser() {
        return counterIdUser++;
    }

    @Override
    public User createUser(User user) {
        for (User u : users.values()) {
            if (u.getEmail().equals(user.getEmail())) {
                throw new EmailExistException("Пользователь с таким email уже зарегестрирован");
            }
        }
        user.setId(generateIdUser());
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public List<User> getListAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("Пользователя с id %d нет.", id));
        }
        return users.get(id);
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException(String.format("Пользователя с id %d нет.", user.getId()));
        }
        for (User u : users.values()) {
            if (u.getEmail().equals(user.getEmail()) && !(u.getId().equals(user.getId()))) {
                throw new EmailExistException("Пользователь с таким email уже зарегестрирован");
            }
        }
        if (user.getName() == null) {
            user.setName(users.get(user.getId()).getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(users.get(user.getId()).getEmail());
        }
        user.setId(user.getId());
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public void deleteUserById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("Пользователя с id %d нет.", id));
        }
        users.remove(id);
    }

}
