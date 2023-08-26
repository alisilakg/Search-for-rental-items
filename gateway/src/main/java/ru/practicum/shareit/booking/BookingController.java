package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.BookingItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.HashMap;
import java.util.Map;
import ru.practicum.shareit.exception.ValidationException;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
   // private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

//    @Autowired
//    public BookingController(BookingService bookingService) {
//        this.bookingService = bookingService;
//    }

   // @ResponseBody
//    @PostMapping
//    public BookingDto create(@Valid @RequestBody BookingItemRequestDto bookingInputDto,
//                             @RequestHeader(USER_ID) Long bookerId) {
//        log.info("Получен POST-запрос к эндпоинту: '/bookings' " +
//                "на создание бронирования от пользователя с ID={}", bookerId);
//        if (bookingInputDto.getEnd().isBefore(bookingInputDto.getStart())) {
//            log.error("End of booking is before start");
//            throw new ValidationException("Время окончания броннирования не может быть раньше времени начала.");
//        }
//        if (bookingInputDto.getEnd().isEqual(bookingInputDto.getStart())) {
//            log.error("End of booking is equals start");
//            throw new ValidationException("Время окончания броннирования не может быть равно времени начала.");
//        }
//        return bookingClient.create(bookingInputDto, bookerId);
//    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookingItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        if (requestDto.getEnd().isBefore(requestDto.getStart())) {
            log.error("End of booking is before start");
            throw new ValidationException("Время окончания броннирования не может быть раньше времени начала.");
        }
        if (requestDto.getEnd().isEqual(requestDto.getStart())) {
            log.error("End of booking is equals start");
            throw new ValidationException("Время окончания броннирования не может быть равно времени начала.");
        }
        return bookingClient.bookItem(userId, requestDto);
    }

//    @ResponseBody
//    @PatchMapping("/{bookingId}")
//    public BookingDto update(@PathVariable Long bookingId,
//                             @RequestHeader(USER_ID) Long userId, @RequestParam Boolean approved) {
//        log.info("Получен PATCH-запрос к эндпоинту: '/bookings' на обновление статуса бронирования с ID={}", bookingId);
//        return bookingService.update(bookingId, userId, approved);
//    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable Long bookingId,
                                                @RequestParam boolean approved) {
        log.info("Approve status of booking {}", bookingId);
        return bookingClient.approveStatus(userId, bookingId, approved);
    }

//    @GetMapping("/{bookingId}")
//    public BookingDto getBookingById(@PathVariable Long bookingId, @RequestHeader(USER_ID) Long userId) {
//        log.info("Получен GET-запрос к эндпоинту: '/bookings' на получение бронирования с ID={}", bookingId);
//        return bookingService.getBookingById(bookingId, userId);
//    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

//    @GetMapping("/owner")
//    public List<BookingDto> getBookingsOwner(@RequestParam(name = "state", defaultValue = "ALL") String state,
//                                             @RequestHeader(USER_ID) Long userId,
//                                             @RequestParam(defaultValue = "0") @Min(value = 0,
//                                                     message = "Индекс первого элемента не может быть отрицательным") int from,
//                                             @RequestParam(defaultValue = "10") @Positive(
//                                                     message = "Количество элементов для отображения должно быть положительным") int size) {
//        log.info("Получен GET-запрос к эндпоинту: '/bookings/owner' на получение " +
//                "списка всех бронирований вещей пользователя с ID={} с параметром STATE={}", userId, state);
//        return bookingService.getBookingsOwner(state, userId, from, size);
//    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingCurrentOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                         Integer from,
                                                         @Positive @RequestParam(name = "size", defaultValue = "10")
                                                         Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking owner with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookingCurrentOwner(userId, state, from, size);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, String>> errorHandler(IllegalArgumentException ex) {
        Map<String, String> resp = new HashMap<>();
        resp.put("error", String.format("Unknown state: UNSUPPORTED_STATUS"));
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }
}
