package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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


