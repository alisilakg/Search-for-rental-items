package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;

import javax.validation.Valid;

@Controller
@RequestMapping("/users")
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;
//    private final UserService userService;
//
//    @Autowired
//    public UserController(UserService userService) {
//        this.userService = userService;
//    }

//    @PostMapping
//    public ResponseEntity<UserDto> create(@RequestBody UserDto userDto) {
//        log.info("POST request received: {}", userDto);
//        return ResponseEntity.ok(userService.createUser(userDto));
//    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserRequestDto requestDto) {
        log.info("Creating user");
        return userClient.createUser(requestDto);
    }

//    @PatchMapping("/{id}")
//    public ResponseEntity<UserDto> update(@RequestBody UserDto userDto, @PathVariable("id") Long id) {
//        log.info("Получен PATCH-запрос к эндпоинту: '/users' на обновление пользователя с ID={}", id);
//        return ResponseEntity.ok(userService.updateUser(id, userDto));
//    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@RequestBody UserRequestDto requestDto,
                                             @PathVariable Long id) {
        log.info("Update user {}", id);
        return userClient.updateUser(id, requestDto);
    }

//    @GetMapping
//    public ResponseEntity<List<UserDto>> findAll() {
//        log.info("Получен GET-запрос к эндпоинту: '/users' на получение всех пользователей");
//        return ResponseEntity.ok(userService.getListAllUsers());
//    }


    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Get all users");
        return userClient.getUsers();
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<UserDto> findById(@PathVariable Long id) {
//        log.info("Получен GET-запрос к эндпоинту: '/users/{id}' на получение пользователя с ID={}", id);
//        return ResponseEntity.ok(userService.getUserById(id));
//    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) {
        log.info("Get user {}", id);
        return userClient.getUser(id);
    }


//    @DeleteMapping("/{id}")
//    public void deleteUserById(@PathVariable Long id) {
//        log.info("User deleted. Id: {}", id);
//        userService.deleteUserById(id);
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        log.info("Delete user {}", id);
        return userClient.deleteUser(id);
    }

}
