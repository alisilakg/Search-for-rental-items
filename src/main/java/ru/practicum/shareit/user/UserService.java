package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.user.UserMapper.toUser;
import static ru.practicum.shareit.user.UserMapper.toUserDto;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto createUser(UserDto userDto) {
        return toUserDto(userStorage.createUser(toUser(userDto)));
    }

    public List<UserDto> getListAllUsers() {
        return userStorage.getListAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(toList());
    }

    public UserDto getUserById(Long id) {
        return toUserDto(userStorage.getUserById(id));
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        if (userDto.getId() == null) {
            userDto.setId(id);
        }
        return toUserDto(userStorage.updateUser(toUser(userDto)));
    }

    public void deleteUserById(Long id) {
        userStorage.deleteUserById(id);
    }
}
