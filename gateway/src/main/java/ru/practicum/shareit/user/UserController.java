package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;

import static ru.practicum.shareit.validation.ValidationGroups.Create;
import static ru.practicum.shareit.validation.ValidationGroups.Update;

@Controller
@RequestMapping("/users")
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Validated(Create.class) UserRequestDto requestDto) {
        log.info("Получен POST-запрос к эндпоинту: '/users' на создание пользователя");
        return userClient.createUser(requestDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@RequestBody @Validated(Update.class) UserRequestDto requestDto,
                                             @PathVariable Long id) {
        log.info("Получен PATCH-запрос к эндпоинту: '/users' на обновление пользователя с ID={}", id);
        return userClient.updateUser(id, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Получен GET-запрос к эндпоинту: '/users' на получение всех пользователей");
        return userClient.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) {
        log.info("Получен GET-запрос к эндпоинту: '/users/{id}' на получение пользователя с ID={}", id);
        return userClient.getUser(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        log.info("Получен DELETE-запрос к эндпоинту: '/users/{id}' на удаление пользователя с ID={}", id);
        return userClient.deleteUser(id);
    }

}
