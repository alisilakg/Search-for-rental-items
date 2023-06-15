package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("POST request received: {}", userDto);
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            log.error("User email empty");
            throw new ValidationException("Адрес электронной почты не может быть пустым.");
        }
        if (!userDto.getEmail().contains("@")) {
            log.error("User email does not contain the symbol @");
            throw new ValidationException("Адрес электронной почты должен содержать символ @.");
        }
        log.info("User added: {}", userDto);
        return userService.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable("id") Long id) {
        log.info("Получен PATCH-запрос к эндпоинту: '/users' на обновление пользователя с ID={}", id);
        return userService.updateUser(id, userDto);
    }

    @GetMapping
    public List<UserDto> findAll() {
        log.info("Получен GET-запрос к эндпоинту: '/users' на получение всех пользователей");
        return userService.getListAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable Long id) {
        log.info("Получен GET-запрос к эндпоинту: '/users/{id}' на получение пользователя с ID={}", id);
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        log.info("User deleted. Id: {}", id);
        userService.deleteUserById(id);
    }

}


