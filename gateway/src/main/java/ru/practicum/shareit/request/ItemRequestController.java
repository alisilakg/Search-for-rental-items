package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/requests")
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

//    private static final String OWNER = "X-Sharer-User-Id";
//    private final ItemRequestService itemRequestService;
//
//    @Autowired
//    public ItemRequestController(ItemRequestService itemRequestService) {
//        this.itemRequestService = itemRequestService;
//    }

//    @ResponseBody
//    @PostMapping
//    public ResponseEntity<ItemRequestDto> create(@Valid @RequestBody ItemRequestDto itemRequestInputDto, @RequestHeader(OWNER) Long ownerId) {
//        log.info("POST request received: {}", itemRequestInputDto);
//        return ResponseEntity.ok(itemRequestService.createItemRequest(itemRequestInputDto, ownerId, LocalDateTime.now()));
//    }

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @Valid @RequestBody ItemRequestRequestDto requestDto) {
        log.info("Create item request by user {}", userId);
        return itemRequestClient.createItemRequest(userId, requestDto);
    }

//    @GetMapping
//    public ResponseEntity<List<ItemRequestDto>> getItemRequestsByRequester(@RequestHeader(OWNER) Long userId) {
//        log.info("Получен GET-запрос к эндпоинту: '/requests' на получение списка собственных запросов пользователя с ID={}", userId);
//        return ResponseEntity.ok(itemRequestService.getItemRequestsByRequesterId(userId));
//    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get all user {} item requests", userId);
        return itemRequestClient.getItemRequestsByUser(userId);
    }

//    @GetMapping("/all")
//    public ResponseEntity<List<ItemRequestDto>> getItemRequests(@RequestHeader(OWNER) Long userId,
//                                                @RequestParam(defaultValue = "0") @Min(value = 0,
//                                                        message = "Индекс первого элемента не может быть отрицательным") int from,
//                                                @RequestParam(defaultValue = "10") @Positive(
//                                                        message = "Количество элементов для отображения должно быть положительным") int size) {
//        log.info("Получен GET-запрос к эндпоинту: '/requests' на получение списка всех запросов на вещи");
//        return ResponseEntity.ok(itemRequestService.getItemRequests(userId, from, size));
//    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all item requests without user {}", userId);
        return itemRequestClient.getAll(userId, from, size);
    }

//
//    @GetMapping("/{requestId}")
//    public ResponseEntity<ItemRequestDto> getItemRequestById(@PathVariable Long requestId, @RequestHeader(OWNER) Long userId) {
//        log.info("Получен GET-запрос к эндпоинту: '/requests' на получение запроса вещи с ID={}", requestId);
//        return ResponseEntity.ok(itemRequestService.getItemRequestById(requestId, userId));
//    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@PathVariable Long requestId,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get item request {}", requestId);
        return itemRequestClient.getItemRequest(requestId, userId);
    }
}
