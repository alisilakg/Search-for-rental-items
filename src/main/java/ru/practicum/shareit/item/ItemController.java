package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private static final String OWNER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @ResponseBody
    @PostMapping
    public ItemDto create(@RequestBody ItemDto itemDto, @RequestHeader(OWNER) Long ownerId) {
        log.info("POST request received: {}", itemDto);
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            log.error("Item name empty");
            throw new ValidationException("Название вещи не может быть пустым.");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            log.error("Item description empty");
            throw new ValidationException("Описание вещи не может быть пустым.");
        }
        if (itemDto.getAvailable() == null) {
            log.error("Item availability empty");
            throw new ValidationException("Доступность вещи не может быть пустой.");
        }
        return itemService.createItem(itemDto, ownerId);
    }

    @ResponseBody
    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                          @RequestHeader(OWNER) Long ownerId) {
        log.info("Получен PATCH-запрос к эндпоинту: '/items' на обновление вещи с ID={}", itemId);
        return itemService.updateItem(itemDto, ownerId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId, @RequestHeader(OWNER) Long userId) {
        log.info("Получен GET-запрос к эндпоинту: '/items' на получение вещи с ID={}", itemId);
        return itemService.getItemById(itemId, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteItemById(@PathVariable Long id) {
        log.info("Item deleted. Id: {}", id);
        itemService.deleteItemById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearchQuery(@RequestParam String text) {
        log.info("Получен GET-запрос к эндпоинту: '/items/search' на поиск вещи с текстом={}", text);
        return itemService.getItemsBySearchQuery(text);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(OWNER) Long ownerId) {
        log.info("Получен GET-запрос к эндпоинту: '/items' на получение всех вещей владельца с ID={}", ownerId);
        return itemService.getItemsByOwner(ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto, @RequestHeader(OWNER) Long userId,
                                    @PathVariable Long itemId) {
        log.info("Получен POST-запрос к эндпоинту: '/items/comment' на" +
                " добавление отзыва пользователем с ID={}", userId);
        return itemService.createComment(commentDto, itemId, userId);
    }
}
