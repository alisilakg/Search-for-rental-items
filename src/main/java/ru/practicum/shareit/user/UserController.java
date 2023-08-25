package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<UserDto> create(@RequestBody UserDto userDto) {
        log.info("POST request received: {}", userDto);
        return ResponseEntity.ok(userService.createUser(userDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> update(@RequestBody UserDto userDto, @PathVariable("id") Long id) {
        log.info("Получен PATCH-запрос к эндпоинту: '/users' на обновление пользователя с ID={}", id);
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        log.info("Получен GET-запрос к эндпоинту: '/users' на получение всех пользователей");
        return ResponseEntity.ok(userService.getListAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable Long id) {
        log.info("Получен GET-запрос к эндпоинту: '/users/{id}' на получение пользователя с ID={}", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        log.info("User deleted. Id: {}", id);
        userService.deleteUserById(id);
    }

}
