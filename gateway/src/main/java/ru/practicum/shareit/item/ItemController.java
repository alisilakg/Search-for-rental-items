package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Controller
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

//    @ResponseBody
//    @PostMapping
//    public ItemDto create(@Valid @RequestBody ItemDto itemDto, @RequestHeader(OWNER) Long ownerId) {
//        log.info("POST request received: {}", itemDto);
//        return itemService.createItem(itemDto, ownerId);
//    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody @Valid ItemRequestDto requestDto) {
        log.info("Create item");
        return itemClient.createItem(userId, requestDto);
    }

//    @ResponseBody
//    @PatchMapping("/{itemId}")
//    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
//                          @RequestHeader(OWNER) Long ownerId) {
//        log.info("Получен PATCH-запрос к эндпоинту: '/items' на обновление вещи с ID={}", itemId);
//        return itemService.updateItem(itemDto, ownerId, itemId);
//    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemRequestDto requestDto,
                                             @PathVariable Long id,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Update item {}", id);
        return itemClient.updateItem(requestDto, id, userId);
    }

//    @GetMapping("/{itemId}")
//    public ItemDto getItemById(@PathVariable Long itemId, @RequestHeader(OWNER) Long userId) {
//        log.info("Получен GET-запрос к эндпоинту: '/items' на получение вещи с ID={}", itemId);
//        return itemService.getItemById(itemId, userId);
//    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@PathVariable Long id,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get item {}", id);
        return itemClient.getItem(id, userId);
    }

//    @DeleteMapping("/{itemId}")
//    public void deleteItemById(@PathVariable Long itemId, @RequestHeader(OWNER) Long userId) {
//        log.info("Item deleted. Id: {}", itemId);
//        itemService.deleteItemById(itemId, userId);
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(@PathVariable Long id) {
        log.info("Delete item {}", id);
        return itemClient.deleteItem(id);
    }

//    @GetMapping("/search")
//    public List<ItemDto> getItemsBySearchQuery(@RequestParam String text,
//                                               @RequestParam(defaultValue = "0") @Min(value = 0,
//                                                       message = "Индекс первого элемента не может быть отрицательным") int from,
//                                               @RequestParam(defaultValue = "10") @Positive(
//                                                       message = "Количество элементов для отображения должно быть положительным") int size) {
//        log.info("Получен GET-запрос к эндпоинту: '/items/search' на поиск вещи с текстом={}", text);
//        return itemService.getItemsBySearchQuery(text, from, size);
//    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Search items by text {}", text);
        return itemClient.searchItem(text, from, size);
    }

//    @GetMapping
//    public List<ItemDto> getItemsByOwner(@RequestHeader(OWNER) Long ownerId,
//                                         @RequestParam(defaultValue = "0") @Min(value = 0,
//                                                 message = "Индекс первого элемента не может быть отрицательным") int from,
//                                         @RequestParam(defaultValue = "10") @Positive(
//                                                 message = "Количество элементов для отображения должно быть положительным") int size) {
//        log.info("Получен GET-запрос к эндпоинту: '/items' на получение всех вещей владельца с ID={}", ownerId);
//        return itemService.getItemsByOwner(ownerId, from, size);
//    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all items from user {}", userId);
        return itemClient.getItems(userId, from, size);
    }

//    @PostMapping("/{itemId}/comment")
//    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto, @RequestHeader(OWNER) Long userId,
//                                    @PathVariable Long itemId) {
//        log.info("Получен POST-запрос к эндпоинту: '/items/comment' на" +
//                " добавление отзыва пользователем с ID={}", userId);
//        return itemService.createComment(commentDto, itemId, userId);
//    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody CommentRequestDto requestDto) {
        log.info("Create comment to item {}", itemId);
        return itemClient.createComment(itemId, userId, requestDto);
    }

}
