package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import static ru.practicum.shareit.validation.ValidationGroups.Create;
import static ru.practicum.shareit.validation.ValidationGroups.Update;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Controller
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor
public class ItemController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID) Long userId,
                                             @RequestBody @Validated(Create.class) ItemRequestDto requestDto) {
        log.info("Получен POST-запрос к эндпоинту: '/items' на создание вещи");
        return itemClient.createItem(userId, requestDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestBody @Validated(Update.class) ItemRequestDto requestDto,
                                             @PathVariable Long id,
                                             @RequestHeader(USER_ID) Long userId) {
        log.info("Получен PATCH-запрос к эндпоинту: '/items' на обновление вещи с ID={}", id);
        return itemClient.updateItem(requestDto, id, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@PathVariable Long id,
                                          @RequestHeader(USER_ID) Long userId) {
        log.info("Получен GET-запрос к эндпоинту: '/items' на получение вещи с ID={}", id);
        return itemClient.getItem(id, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(@PathVariable Long id) {
        log.info("Получен DELETE-запрос к эндпоинту: '/items' на удаление вещи с ID={}", id);
        return itemClient.deleteItem(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получен GET-запрос к эндпоинту: '/items/search' на поиск вещи с текстом={}", text);
        return itemClient.searchItem(text, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(USER_ID) Long userId,
                                           @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получен GET-запрос к эндпоинту: '/items' на получение всех вещей владельца с ID={}", userId);
        return itemClient.getItems(userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long itemId, @RequestHeader(USER_ID) Long userId,
                                                @Validated(Create.class) @RequestBody CommentRequestDto requestDto) {
        log.info("Получен POST-запрос к эндпоинту: '/items/comment' на добавление отзыва пользователем с ID={}", userId);
        return itemClient.createComment(itemId, userId, requestDto);
    }

}
