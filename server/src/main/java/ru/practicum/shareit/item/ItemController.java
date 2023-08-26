package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private static final String OWNER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @ResponseBody
    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestBody ItemDto itemDto, @RequestHeader(OWNER) Long ownerId) {
        log.info("Получен POST-запрос к эндпоинту: '/items' на создание вещи");
        return ResponseEntity.ok(itemService.createItem(itemDto, ownerId));
    }

    @ResponseBody
    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                          @RequestHeader(OWNER) Long ownerId) {
        log.info("Получен PATCH-запрос к эндпоинту: '/items' на обновление вещи с ID={}", itemId);
        return ResponseEntity.ok(itemService.updateItem(itemDto, ownerId, itemId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long itemId, @RequestHeader(OWNER) Long userId) {
        log.info("Получен GET-запрос к эндпоинту: '/items' на получение вещи с ID={}", itemId);
        return ResponseEntity.ok(itemService.getItemById(itemId, userId));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@PathVariable Long itemId, @RequestHeader(OWNER) Long userId) {
        log.info("Получен DELETE-запрос к эндпоинту: '/items' на удаление вещи с ID={}", itemId);
        itemService.deleteItemById(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> getItemsBySearchQuery(@RequestParam String text,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size) {
        log.info("Получен GET-запрос к эндпоинту: '/items/search' на поиск вещи с текстом={}", text);
        return ResponseEntity.ok(itemService.getItemsBySearchQuery(text, from, size));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItemsByOwner(@RequestHeader(OWNER) Long ownerId,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        log.info("Получен GET-запрос к эндпоинту: '/items' на получение всех вещей владельца с ID={}", ownerId);
        return ResponseEntity.ok(itemService.getItemsByOwner(ownerId, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto commentDto, @RequestHeader(OWNER) Long userId,
                                    @PathVariable Long itemId) {
        log.info("Получен POST-запрос к эндпоинту: '/items/comment' на добавление отзыва пользователем с ID={}", userId);
        return ResponseEntity.ok(itemService.createComment(commentDto, itemId, userId));
    }
}
