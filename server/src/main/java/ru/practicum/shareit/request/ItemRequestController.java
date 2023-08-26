package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@Validated
public class ItemRequestController {

    private static final String OWNER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @ResponseBody
    @PostMapping
    public ResponseEntity<ItemRequestDto> create(@RequestBody ItemRequestDto itemRequestInputDto, @RequestHeader(OWNER) Long ownerId) {
        log.info("POST request received: {}", itemRequestInputDto);
        return ResponseEntity.ok(itemRequestService.createItemRequest(itemRequestInputDto, ownerId, LocalDateTime.now()));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getItemRequestsByRequester(@RequestHeader(OWNER) Long userId) {
        log.info("Получен GET-запрос к эндпоинту: '/requests' на получение списка собственных запросов пользователя с ID={}", userId);
        return ResponseEntity.ok(itemRequestService.getItemRequestsByRequesterId(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getItemRequests(@RequestHeader(OWNER) Long userId,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        log.info("Получен GET-запрос к эндпоинту: '/requests' на получение списка всех запросов на вещи");
        return ResponseEntity.ok(itemRequestService.getItemRequests(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getItemRequestById(@PathVariable Long requestId, @RequestHeader(OWNER) Long userId) {
        log.info("Получен GET-запрос к эндпоинту: '/requests' на получение запроса вещи с ID={}", requestId);
        return ResponseEntity.ok(itemRequestService.getItemRequestById(requestId, userId));
    }
}
