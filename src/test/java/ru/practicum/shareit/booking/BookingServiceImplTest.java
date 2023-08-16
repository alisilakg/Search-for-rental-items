package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.CheckService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingTimeDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository mockBookingRepository;
    @Mock
    private BookingMapper mockBookingMapper;
    @Mock
    private CheckService mockCheckService;
    @InjectMocks
    private BookingServiceImpl bookingService;
    private User booker;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingInputDto bookingToSave;
    private BookingDto expectedBooking;

    private BookingTimeDto bookingTimeDto;
    private ItemDto itemToSave;
    @BeforeEach
    void setup() {
        booker = new User();
        booker.setName("name");
        booker.setEmail("e@mail.ru");
        booker.setId(1L);

        UserDto bookerDto = new UserDto();
        bookerDto.setName("name");
        bookerDto.setEmail("e@mail.ru");
        bookerDto.setId(1L);

        owner = new User();
        owner.setName("name2");
        owner.setEmail("e2@mail.ru");
        owner.setId(2L);

        item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequestId(1L);

        itemToSave = new ItemDto();
        itemToSave.setId(1L);
        itemToSave.setName("name");
        itemToSave.setDescription("description");
        itemToSave.setAvailable(true);
        itemToSave.setRequestId(1L);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2023,1,1,1,1));
        booking.setEnd(LocalDateTime.of(2024,1,3,1,1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.APPROVED);

        bookingToSave = new BookingInputDto();
        bookingToSave.setStart(LocalDateTime.of(2023,1,1,1,1));
        bookingToSave.setEnd(LocalDateTime.of(2023,1,3,1,1));
        bookingToSave.setItemId(item.getId());

        expectedBooking = new BookingDto();
        expectedBooking.setId(1L);
        expectedBooking.setStart(LocalDateTime.of(2023,1,1,1,1));
        expectedBooking.setEnd(LocalDateTime.of(2023,1,3,1,1));
        expectedBooking.setItem(itemToSave);
        expectedBooking.setBooker(bookerDto);
        expectedBooking.setStatus(Status.APPROVED);

        bookingTimeDto = new BookingTimeDto();
        bookingTimeDto.setId(1L);
        bookingTimeDto.setBookerId(1L);
        bookingTimeDto.setStart(LocalDateTime.of(2023,1,1,1,1));
        bookingTimeDto.setEnd(LocalDateTime.of(2024,1,3,1,1));
    }

    @Test
    void createBooking_whenBookingValid_thenSaveBooking() {
        when(mockCheckService.isExistUser(booker.getId())).thenReturn(true);
        when(mockCheckService.isAvailableItem(bookingToSave)).thenReturn(true);
        when(mockBookingMapper.toBooking(bookingToSave, booker.getId())).thenReturn(booking);
        when(mockBookingMapper.toBookingDto(booking)).thenReturn(expectedBooking);
        when(mockBookingRepository.save(any())).thenReturn(booking);

        BookingDto actualBookingDto = bookingService.create(bookingToSave,
                booker.getId());

        assertNotNull(actualBookingDto);
        assertEquals(booking.getId(), actualBookingDto.getId());
        assertThat(actualBookingDto.getStart(), equalTo(expectedBooking.getStart()));
        assertThat(actualBookingDto.getEnd(), equalTo(expectedBooking.getEnd()));
        assertThat(actualBookingDto.getItem(), equalTo(expectedBooking.getItem()));
        assertThat(actualBookingDto.getBooker(), equalTo(expectedBooking.getBooker()));
        assertThat(actualBookingDto.getStatus(), equalTo(expectedBooking.getStatus()));
        verify(mockBookingRepository, times(1)).save(any());
    }

    @Test
    void createBooking_whenBookerNotFound_thenReturnedNotFoundExceptionThrown() {
        long bookerNotFoundId = 0L;
        String error = String.format("Пользователь с ID=" + bookerNotFoundId + " не найден!");
        when(mockCheckService.isExistUser(bookerNotFoundId)).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.create(bookingToSave, bookerNotFoundId)
        );

        assertEquals(error, exception.getMessage());
        verify(mockBookingRepository, times(0)).save(any());
    }

    @Test
    void createBooking_whenItemNotAvailable_thenReturnedValidationExceptionThrown() {
        String error = String.format("Вещь с ID=" + bookingToSave.getItemId() +
                " недоступна для бронирования!");
        when(mockCheckService.isExistUser(booker.getId())).thenReturn(true);
        when(mockCheckService.isAvailableItem(bookingToSave)).thenReturn(false);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.create(bookingToSave, booker.getId())
        );

        assertEquals(error, exception.getMessage());
        verify(mockBookingRepository, times(0)).save(any());
    }

    @Test
    void createBooking_whenItemIsNotAvailable_thenReturnedValidationExceptionThrown() {
        String error = String.format("Вещь с ID=" + itemToSave.getId() +
                " недоступна для бронирования!");
        when(mockCheckService.isExistUser(booker.getId())).thenReturn(true);
        when(mockCheckService.isAvailableItem(bookingToSave)).thenThrow(new ValidationException(error));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.create(bookingToSave, booker.getId())
        );

        assertEquals(error, exception.getMessage());
        verify(mockBookingRepository, times(0)).save(any());
    }

    @Test
    void createBooking_whenBookerIsOwner_thenReturnedNotFoundExceptionThrown() {
        String error = String.format("Вещь с ID=" + bookingToSave.getItemId() +
                " недоступна для бронирования самим владельцем!");
        when(mockCheckService.isExistUser(owner.getId())).thenReturn(true);
        when(mockCheckService.isAvailableItem(bookingToSave)).thenReturn(true);
        when(mockBookingMapper.toBooking(any(), any())).thenReturn(booking);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.create(bookingToSave, owner.getId())
        );

        assertEquals(error, exception.getMessage());
        verify(mockBookingRepository, times(0)).save(any());
    }

    @Test
    void updateBooking_whenOwnerCanceledBooking_thenReturnedValidationExceptionThrown() {
        long bookingId = booking.getId();
        long userId = owner.getId();
        boolean approved = false;
        String error = "Бронирование было отменено!";
        booking.setStatus(Status.CANCELED);
        when(mockCheckService.isItemOwner(booking.getItem().getId(), userId)).thenReturn(true);
        when(mockCheckService.isExistUser(userId)).thenReturn(true);
        when(mockBookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.update(bookingId, userId, approved)
        );

        assertEquals(error, exception.getMessage());
        verify(mockBookingRepository, times(0)).save(any());
    }

    @Test
    void updateBooking_whenTimeNotValid_thenReturnedValidationExceptionThrown() {
        long bookingId = booking.getId();
        long userId = owner.getId();
        boolean approved = true;
        String error = "Время бронирования уже истекло!";
        booking.setEnd(LocalDateTime.now().minusHours(1));
        when(mockCheckService.isExistUser(userId)).thenReturn(true);
        when(mockBookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.update(bookingId, userId, approved)
        );

        assertEquals(error, exception.getMessage());
        verify(mockBookingRepository, times(0)).save(any());
    }

    @Test
    void updateBooking_whenStatusIsCanceled_thenReturnedValidationExceptionThrown() {
        long bookingId = booking.getId();
        long userId = owner.getId();
        boolean approved = true;
        String error = "Решение по бронированию уже принято!";
        expectedBooking.setStatus(Status.CANCELED);
        when(mockCheckService.isExistUser(userId)).thenReturn(true);
        when(mockCheckService.isItemOwner(booking.getItem().getId(), userId)).thenReturn(true);
        when(mockBookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.update(bookingId, userId, approved)
        );

        assertEquals(error, exception.getMessage());
        verify(mockBookingRepository, times(0)).save(any());
    }

    @Test
    void updateBooking_whenBookerApprovedBooking_thenReturnedNotFoundExceptionThrown() {
        long bookingId = booking.getId();
        long userId = booker.getId();
        boolean approved = true;
        String error = "Подтвердить бронирование может только владелец вещи!";
        when(mockCheckService.isExistUser(userId)).thenReturn(true);
        when(mockBookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.update(bookingId, userId, approved)
        );

        assertEquals(error, exception.getMessage());
        verify(mockBookingRepository, times(0)).save(any());
    }

    @Test
    void updateBooking_whenOwnerNotCanceledBooking_thenReturnedBookingStatusRejected() {
        long bookingId = booking.getId();
        long userId = owner.getId();
        boolean notApproved = false;
        booking.setStatus(Status.WAITING);
        expectedBooking.setStatus(Status.REJECTED);
        when(mockCheckService.isItemOwner(booking.getItem().getId(), userId)).thenReturn(true);
        when(mockCheckService.isExistUser(userId)).thenReturn(true);
        when(mockBookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(mockBookingMapper.toBookingDto(booking)).thenReturn(expectedBooking);
        when(mockBookingRepository.save(any())).thenReturn(booking);

        BookingDto actualBooking = bookingService.update(bookingId, userId, notApproved);

        assertThat(actualBooking.getStatus(), equalTo(Status.REJECTED));
    }

    @Test
    void getBookingById_whenBookingFoundAndUserIsOwner_thenReturnedBooking() {
        long ownerId = owner.getId();
        long expectedBookingId = expectedBooking.getId();
        when(mockCheckService.isExistUser(ownerId)).thenReturn(true);
        when(mockCheckService.isItemOwner(item.getId(), ownerId)).thenReturn(true);
        when(mockBookingMapper.toBookingDto(booking)).thenReturn(expectedBooking);
        when(mockBookingRepository.findById(expectedBookingId)).thenReturn(Optional.of(booking));

        BookingDto actualBooking = bookingService.getBookingById(expectedBookingId, ownerId);

        assertThat(actualBooking.getStart(), equalTo(expectedBooking.getStart()));
        assertThat(actualBooking.getEnd(), equalTo(expectedBooking.getEnd()));
        assertThat(actualBooking.getItem(), equalTo(expectedBooking.getItem()));
        assertThat(actualBooking.getBooker(), equalTo(expectedBooking.getBooker()));
        assertThat(actualBooking.getStatus(), equalTo(expectedBooking.getStatus()));
    }

    @Test
    void getBookingById_whenBookingFoundAndUserIsBooker_thenReturnedBooking() {
        long bookerId = booker.getId();
        long expectedBookingId = expectedBooking.getId();
        when(mockCheckService.isExistUser(bookerId)).thenReturn(true);
        when(mockCheckService.isItemBooker(booking, bookerId)).thenReturn(true);
        when(mockBookingMapper.toBookingDto(booking)).thenReturn(expectedBooking);
        when(mockBookingRepository.findById(expectedBookingId)).thenReturn(Optional.of(booking));

        BookingDto actualBooking = bookingService.getBookingById(expectedBookingId, bookerId);

        assertThat(actualBooking.getStart(), equalTo(expectedBooking.getStart()));
        assertThat(actualBooking.getEnd(), equalTo(expectedBooking.getEnd()));
        assertThat(actualBooking.getItem(), equalTo(expectedBooking.getItem()));
        assertThat(actualBooking.getBooker(), equalTo(expectedBooking.getBooker()));
        assertThat(actualBooking.getStatus(), equalTo(expectedBooking.getStatus()));
    }

    @Test
    void getBookingById_whenBookingNotFound_thenNotFoundExceptionThrown() {
        long bookingNotFoundId = 0L;
        String error = String.format("Бронирование с ID=" + bookingNotFoundId + " не найдено!");
        when(mockCheckService.isExistUser(any())).thenReturn(true);
        when(mockBookingRepository.findById(bookingNotFoundId)).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingById(bookingNotFoundId, any())
        );

        assertEquals(error, exception.getMessage());
    }

    @Test
    void getBookingById_whenBookerNotFound_thenNotFoundExceptionThrown() {
        long bookerNotFoundId = 0L;
        String error = String.format("Пользователь с ID=" + bookerNotFoundId + " не найден!");
        when(mockCheckService.isExistUser(bookerNotFoundId)).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingById(booking.getId(), bookerNotFoundId)
        );

        assertEquals(error, exception.getMessage());
    }

    @Test
    void getBookingById_whenOwnerNotFound_thenNotFoundExceptionThrown() {
        long ownerNotFoundId = 0L;
        String error = String.format("Пользователь с ID=" + ownerNotFoundId + " не найден!");
        when(mockCheckService.isExistUser(ownerNotFoundId)).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingById(booking.getId(), ownerNotFoundId)
        );

        assertEquals(error, exception.getMessage());
    }

    @Test
    void getBookingById_whenUserIsNotBookerAndIsNotOwner_thenNotFoundExceptionThrown() {
        long userId = 5L;
        long bookingId = booking.getId();
        String error = "Посмотреть данные бронирования может только владелец вещи или бронирующий ее!";
        when(mockCheckService.isExistUser(userId)).thenReturn(true);
        when(mockCheckService.isItemBooker(booking, userId)).thenReturn(false);
        when(mockCheckService.isItemOwner(item.getId(), userId)).thenReturn(false);
        when(mockBookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingById(bookingId, userId)
        );

        assertEquals(error, exception.getMessage());
    }

    @Test
    void getBookings_whenStatusAll_thenReturnedOneBooking() {
        int from = 0;
        int size = 1;
        long userId = booker.getId();
        when(mockCheckService.isExistUser(any())).thenReturn(true);
        when(mockBookingRepository.findByBookerId(anyLong(), any())).thenReturn(List.of(booking));

        List<BookingDto> actualBookings = bookingService.getBookings("ALL", userId, from, size);

        assertNotNull(actualBookings);
        assertEquals(1, actualBookings.size());
    }

    @Test
    void getBookings_whenStatePast_thenReturnedOneBooking() {
        int from = 0;
        int size = 1;
        long userId = booker.getId();
        when(mockCheckService.isExistUser(any())).thenReturn(true);
        when(mockBookingRepository.findByBookerIdAndEndIsBefore(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> actualBookings = bookingService.getBookings("PAST", userId, from, size);

        assertEquals(actualBookings.size(), 1);
    }

    @Test
    void getBookings_whenStateCurrant_thenReturnedOneBooking() {
        int from = 0;
        int size = 1;
        long userId = booker.getId();
        when(mockCheckService.isExistUser(any())).thenReturn(true);
        when(mockBookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> actualBookings = bookingService.getBookings("CURRENT", userId, from, size);

        assertEquals(actualBookings.size(), 1);
    }

    @Test
    void getBookings_whenStateFuture_thenReturnedOneBooking() {
        int from = 0;
        int size = 1;
        long userId = booker.getId();
        when(mockCheckService.isExistUser(any())).thenReturn(true);
        when(mockBookingRepository.findByBookerIdAndStartIsAfter(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> actualBookings = bookingService.getBookings("FUTURE", userId, from, size);

        assertEquals(actualBookings.size(), 1);
    }

    @Test
    void getBookings_whenStateWaiting_thenReturnedOneBooking() {
        int from = 0;
        int size = 1;
        long userId = booker.getId();
        when(mockCheckService.isExistUser(any())).thenReturn(true);
        when(mockBookingRepository.findByBookerIdAndStatus(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> actualBookings = bookingService.getBookings("WAITING", userId, from, size);

        assertEquals(actualBookings.size(), 1);
    }

    @Test
    void getBookings_whenStateRejected_thenReturnedOneBooking() {
        int from = 0;
        int size = 1;
        long userId = booker.getId();
        when(mockCheckService.isExistUser(any())).thenReturn(true);
        when(mockBookingRepository.findByBookerIdAndStatus(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> actualBookings = bookingService.getBookings("REJECTED", userId, from, size);

        assertEquals(actualBookings.size(), 1);
    }

    @Test
    void getBookings_whenStateNotValid_thenValidationExceptionThrown() {
        int from = 0;
        int size = 1;
        long userId = booker.getId();
        String state = "UNKNOWN";
        String error = "Unknown state: " + state;
        when(mockCheckService.isExistUser(any())).thenReturn(true);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.getBookings(state, userId, from, size)
        );

        assertEquals(error, exception.getMessage());
    }

    @Test
    void getBookingsOwner_whenStatusAll_thenReturnedOneBooking() {
        int from = 0;
        int size = 1;
        long userId = booker.getId();
        when(mockCheckService.isExistUser(any())).thenReturn(true);
        when(mockBookingRepository.findByItemOwnerId(anyLong(), any())).thenReturn(List.of(booking));

        List<BookingDto> actualBookings = bookingService.getBookingsOwner("ALL", userId, from, size);

        assertNotNull(actualBookings);
        assertEquals(1, actualBookings.size());
    }

    @Test
    void getBookingsOwner_whenStatePast_thenReturnedOneBooking() {
        int from = 0;
        int size = 1;
        long userId = booker.getId();
        when(mockCheckService.isExistUser(any())).thenReturn(true);
        when(mockBookingRepository.findByItemOwnerIdAndEndIsBefore(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> actualBookings = bookingService.getBookingsOwner("PAST", userId, from, size);

        assertEquals(actualBookings.size(), 1);
    }

    @Test
    void getBookingsOwner_whenStateCurrant_thenReturnedOneBooking() {
        int from = 0;
        int size = 1;
        long userId = booker.getId();
        when(mockCheckService.isExistUser(any())).thenReturn(true);
        when(mockBookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> actualBookings = bookingService.getBookingsOwner("CURRENT", userId, from, size);

        assertEquals(actualBookings.size(), 1);
    }

    @Test
    void getBookingsOwner_whenStateFuture_thenReturnedOneBooking() {
        int from = 0;
        int size = 1;
        long userId = booker.getId();
        when(mockCheckService.isExistUser(any())).thenReturn(true);
        when(mockBookingRepository.findByItemOwnerIdAndStartIsAfter(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> actualBookings = bookingService.getBookingsOwner("FUTURE", userId, from, size);

        assertEquals(actualBookings.size(), 1);
    }

    @Test
    void getBookingsOwner_whenStateWaiting_thenReturnedOneBooking() {
        int from = 0;
        int size = 1;
        long userId = booker.getId();
        when(mockCheckService.isExistUser(any())).thenReturn(true);
        when(mockBookingRepository.findByItemOwnerIdAndStatus(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> actualBookings = bookingService.getBookingsOwner("WAITING", userId, from, size);

        assertEquals(actualBookings.size(), 1);
    }

    @Test
    void getBookingsOwner_whenStateRejected_thenReturnedOneBooking() {
        int from = 0;
        int size = 1;
        long userId = booker.getId();
        when(mockCheckService.isExistUser(any())).thenReturn(true);
        when(mockBookingRepository.findByItemOwnerIdAndStatus(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> actualBookings = bookingService.getBookingsOwner("REJECTED", userId, from, size);

        assertEquals(actualBookings.size(), 1);
    }

    @Test
    void getBookingsOwner_whenStateNotValid_thenValidationExceptionThrown() {
        int from = 0;
        int size = 1;
        long userId = booker.getId();
        String state = "UNKNOWN";
        String error = "Unknown state: " + state;
        when(mockCheckService.isExistUser(any())).thenReturn(true);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.getBookingsOwner(state, userId, from, size)
        );

        assertEquals(error, exception.getMessage());
    }

    @Test
    void getLastBooking() {
        when(mockBookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(anyLong(), any(), any())).thenReturn(booking);
        when(mockBookingMapper.toBookingTimeDto(any())).thenReturn(bookingTimeDto);

        BookingTimeDto actualBookingTimeDto = bookingService.getLastBooking(item.getId());

        assertThat(actualBookingTimeDto.getBookerId(), equalTo(booking.getBooker().getId()));
        assertThat(actualBookingTimeDto.getStart(), equalTo(booking.getStart()));
        assertThat(actualBookingTimeDto.getEnd(), equalTo(booking.getEnd()));
    }

    @Test
    void getNextBooking() {
        when(mockBookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(anyLong(), any(), any())).thenReturn(booking);
        when(mockBookingMapper.toBookingTimeDto(any())).thenReturn(bookingTimeDto);

        BookingTimeDto actualBookingTimeDto = bookingService.getNextBooking(item.getId());

        assertEquals(actualBookingTimeDto.getBookerId(), booking.getBooker().getId());
        assertThat(actualBookingTimeDto.getStart(), equalTo(booking.getStart()));
        assertThat(actualBookingTimeDto.getEnd(), equalTo(booking.getEnd()));
    }

    @Test
    void getBookingWithUserBookedItem() {
        when(mockBookingRepository.findFirstByItemIdAndBookerIdAndEndIsBeforeAndStatus(anyLong(), anyLong(), any(), any()))
                .thenReturn(booking);

        Booking actualBooking = bookingService.getBookingWithUserBookedItem(item.getId(), booker.getId());

        assertEquals(actualBooking.getBooker().getId(), booking.getBooker().getId());
    }
}