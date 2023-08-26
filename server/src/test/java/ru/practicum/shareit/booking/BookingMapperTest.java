package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingTimeDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BookingMapperTest {
    @Autowired
    private BookingMapper bookingMapper;

    private User booker;

    private Booking booking;


    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setName("name");
        booker.setEmail("e@mail.ru");
        booker.setId(1L);

        User owner = new User();
        owner.setName("name2");
        owner.setEmail("e2@mail.ru");
        owner.setId(2L);

        Item item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequestId(1L);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2023,1,1,1,1));
        booking.setEnd(LocalDateTime.of(2024,1,3,1,1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.APPROVED);
    }

    @Test
    void toBookingTimeDto() {
        BookingTimeDto expected = new BookingTimeDto();
        expected.setId(1L);
        expected.setBookerId(booker.getId());
        expected.setStart(booking.getStart());
        expected.setEnd(booking.getEnd());

        BookingTimeDto actual = bookingMapper.toBookingTimeDto(booking);

        assertEquals(expected, actual);
    }
}
