package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingTimeDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckServiceTest {
    @Mock
    private UserService mockUserService;

    @Mock
    private  ItemServiceImpl mockItemService;

    @InjectMocks
    private CheckService checkService;

    private User booker;
    private UserDto bookerDto;
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

        bookerDto = new UserDto();
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
    void isAvailableItem() {
        when(mockItemService.findItemById(bookingToSave.getItemId())).thenReturn(item);

        boolean actual = checkService.isAvailableItem(bookingToSave);

        assertTrue(actual);
    }

    @Test
    void isItemOwner() {
        when(mockItemService.getItemsByOwner(owner.getId(), 0, 10)).thenReturn(List.of(itemToSave));

        boolean actual = checkService.isItemOwner(item.getId(), owner.getId());

        assertTrue(actual);
    }

    @Test
    void isItemBooker() {
        boolean actual = checkService.isItemBooker(booking, booker.getId());

        assertTrue(actual);
    }

    @Test
    void isExistUser() {
        when(mockUserService.getUserById(bookerDto.getId())).thenReturn(bookerDto);
        boolean actual = checkService.isExistUser(bookerDto.getId());

        assertTrue(actual);
    }
}
