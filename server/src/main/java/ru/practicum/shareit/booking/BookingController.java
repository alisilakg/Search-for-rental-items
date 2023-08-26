package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @ResponseBody
    @PostMapping
    public ResponseEntity<BookingDto> create(@RequestBody BookingInputDto bookingInputDto,
                             @RequestHeader(USER_ID) Long bookerId) {
        log.info("Получен POST-запрос к эндпоинту: '/bookings' " +
                "на создание бронирования от пользователя с ID={}", bookerId);
        return ResponseEntity.ok(bookingService.create(bookingInputDto, bookerId));
    }

    @ResponseBody
    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> update(@PathVariable Long bookingId,
                             @RequestHeader(USER_ID) Long userId, @RequestParam Boolean approved) {
        log.info("Получен PATCH-запрос к эндпоинту: '/bookings' на обновление статуса бронирования с ID={}", bookingId);
        return ResponseEntity.ok(bookingService.update(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable Long bookingId, @RequestHeader(USER_ID) Long userId) {
        log.info("Получен GET-запрос к эндпоинту: '/bookings' на получение бронирования с ID={}", bookingId);
        return ResponseEntity.ok(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getBookings(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                        @RequestHeader(USER_ID) Long userId,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size) {
        log.info("Получен GET-запрос к эндпоинту: '/bookings' на получение " +
                "списка всех бронирований пользователя с ID={} с параметром STATE={}", userId, state);
        return ResponseEntity.ok(bookingService.getBookings(state, userId, from, size));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getBookingsOwner(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                             @RequestHeader(USER_ID) Long userId,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        log.info("Получен GET-запрос к эндпоинту: '/bookings/owner' на получение " +
                "списка всех бронирований вещей пользователя с ID={} с параметром STATE={}", userId, state);
        return ResponseEntity.ok(bookingService.getBookingsOwner(state, userId, from, size));
    }
}
