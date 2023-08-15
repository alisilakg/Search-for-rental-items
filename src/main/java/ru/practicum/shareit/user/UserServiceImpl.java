package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        isEmailValid(userDto.getEmail());
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(userRepository.save(user));
    }

    void isEmailValid(String email) {
        if (email == null || email.isBlank()) {
            log.error("User email empty");
            throw new ValidationException("Адрес электронной почты не может быть пустым.");
        }
        if (!email.contains("@")) {
            log.error("User email does not contain the symbol @");
            throw new ValidationException("Адрес электронной почты должен содержать символ @.");
        }
    }

    @Override
    public List<UserDto> getListAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        return userMapper.toUserDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id %d не найден", id))));
    }

    @Override
    public UserDto updateUser(Long id, UserDto userToUpdate) {
        if (userToUpdate.getId() == null) {
            userToUpdate.setId(id);
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id %d не найден", id)));
        updateFields(user, userToUpdate);
        return userMapper.toUserDto(userRepository.save(user));
    }
    private void updateFields(User user, UserDto userToUpdate) {
        if (userToUpdate.getName() != null) {
            user.setName(userToUpdate.getName());
        }
        if ((userToUpdate.getEmail() != null) && (!userToUpdate.getEmail().equals(user.getEmail()))) {
            if (userRepository.findByEmail(userToUpdate.getEmail())
                    .stream()
                    .filter(u -> u.getEmail().equals(userToUpdate.getEmail()))
                    .allMatch(u -> u.getId().equals(userToUpdate.getId()))) {
                user.setEmail(userToUpdate.getEmail());
            } else {
                throw new EmailExistException("Пользователь с Email=" + userToUpdate.getEmail() + " уже существует!");
            }
        }
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

}
