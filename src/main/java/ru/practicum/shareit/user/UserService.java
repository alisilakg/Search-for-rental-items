package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserStorage userStorage, UserMapper userMapper) {
        this.userStorage = userStorage;
        this.userMapper = userMapper;
    }

    public UserDto createUser(UserDto userDto) {
        return userMapper.toUserDto(userStorage.createUser(userMapper.toUser(userDto)));
    }

    public List<UserDto> getListAllUsers() {
        return userStorage.getListAllUsers().stream()
                .map(userMapper::toUserDto)
                .collect(toList());
    }

    public UserDto getUserById(Long id) {
        return userMapper.toUserDto(userStorage.getUserById(id));
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        if (userDto.getId() == null) {
            userDto.setId(id);
        }
        return userMapper.toUserDto(userStorage.updateUser(userMapper.toUser(userDto)));
    }

    public void deleteUserById(Long id) {
        userStorage.deleteUserById(id);
    }
}
