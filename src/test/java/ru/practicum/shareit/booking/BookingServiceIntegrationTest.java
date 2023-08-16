package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class BookingServiceIntegrationTest {
    private final EntityManager em;
    private final BookingService service;

    @Test
    void createBooking() {
        User owner = new User(null, "User1", "u@email.com");
        em.persist(owner);

        Item item = new Item(null, "Item", "GoodItem", true, owner, null);
        em.persist(item);

        User booker = new User(null, "Booker", "b@email.com");
        em.persist(booker);

        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);

        BookingInputDto bookingInputDto = makeBookingInputDto(item.getId(), start, end);
        BookingDto actualBooking = service.create(bookingInputDto, booker.getId());

        assertThat(actualBooking.getId(), notNullValue());
        assertThat(actualBooking.getStart(), equalTo(bookingInputDto.getStart()));
        assertThat(actualBooking.getEnd(), equalTo(bookingInputDto.getEnd()));
        assertThat(actualBooking.getItem().getId(), equalTo(bookingInputDto.getItemId()));
        assertThat(actualBooking.getBooker().getId(), equalTo(booker.getId()));
        assertThat(actualBooking.getStatus(), equalTo(Status.WAITING));
    }


    @Test
    void updateBooking() {
        User owner = new User(null, "User1", "u@email.com");
        em.persist(owner);

        Item item = new Item(null, "Item", "GoodItem", true, owner, null);
        em.persist(item);

        User booker = new User(null, "Booker", "b@email.com");
        em.persist(booker);

        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);

        BookingInputDto bookingInputDto = makeBookingInputDto(item.getId(), start, end);
        BookingDto booking = service.create(bookingInputDto, booker.getId());

        BookingDto actualBooking = service.update(booking.getId(), owner.getId(), true);

        assertThat(actualBooking.getId(), notNullValue());
        assertThat(actualBooking.getStart(), equalTo(bookingInputDto.getStart()));
        assertThat(actualBooking.getEnd(), equalTo(bookingInputDto.getEnd()));
        assertThat(actualBooking.getItem().getId(), equalTo(bookingInputDto.getItemId()));
        assertThat(actualBooking.getBooker().getId(), equalTo(booker.getId()));
        assertThat(actualBooking.getStatus(), equalTo(Status.APPROVED));
    }


    @Test
    void getBookingById() {
        User owner = new User(null, "User1", "u@email.com");
        em.persist(owner);

        Item item = new Item(null, "Item", "GoodItem", true, owner, null);
        em.persist(item);

        User booker = new User(null, "Booker", "b@email.com");
        em.persist(booker);

        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);

        BookingInputDto bookingInputDto = makeBookingInputDto(item.getId(), start, end);
        BookingDto booking = service.create(bookingInputDto, booker.getId());

        BookingDto actualBooking = service.getBookingById(booking.getId(), booker.getId());

        assertThat(actualBooking.getId(), notNullValue());
        assertThat(actualBooking.getStart(), equalTo(bookingInputDto.getStart()));
        assertThat(actualBooking.getEnd(), equalTo(bookingInputDto.getEnd()));
        assertThat(actualBooking.getItem().getId(), equalTo(bookingInputDto.getItemId()));
        assertThat(actualBooking.getBooker().getId(), equalTo(booker.getId()));
        assertThat(actualBooking.getStatus(), equalTo(Status.WAITING));
    }


    @Test
    void getBookings() {
        User owner = new User(null, "User1", "u@email.com");
        em.persist(owner);

        Item item = new Item(null, "Item1", "GoodItem", true, owner, null);
        em.persist(item);

        User booker = new User(null, "Booker", "b@email.com");
        em.persist(booker);

        LocalDateTime start = LocalDateTime.now().minusHours(3);
        LocalDateTime end = LocalDateTime.now().plusHours(5);

        BookingInputDto bookingInputDto = makeBookingInputDto(item.getId(), start, end);
        BookingDto booking = service.create(bookingInputDto, booker.getId());

        LocalDateTime start2 = LocalDateTime.now().minusHours(1);
        LocalDateTime end2 = LocalDateTime.now().plusHours(3);

        BookingInputDto bookingInputDto2 = makeBookingInputDto(item.getId(), start2, end2);
        BookingDto booking2 = service.create(bookingInputDto2, booker.getId());

        List<BookingDto> actualBookings = service.getBookings("ALL", booker.getId(), 0, 10);

        assertThat(actualBookings, hasSize(2));
        assertThat(actualBookings.get(0).getId(), equalTo(booking2.getId()));
        assertThat(actualBookings.get(0).getStart(), equalTo(bookingInputDto2.getStart()));
        assertThat(actualBookings.get(0).getEnd(), equalTo(bookingInputDto2.getEnd()));
        assertThat(actualBookings.get(0).getItem().getId(), equalTo(bookingInputDto2.getItemId()));
        assertThat(actualBookings.get(0).getBooker().getId(), equalTo(booker.getId()));
        assertThat(actualBookings.get(0).getStatus(), equalTo(Status.WAITING));
        assertThat(actualBookings.get(1).getId(), equalTo(booking.getId()));
        assertThat(actualBookings.get(1).getStart(), equalTo(bookingInputDto.getStart()));
        assertThat(actualBookings.get(1).getEnd(), equalTo(bookingInputDto.getEnd()));
        assertThat(actualBookings.get(1).getItem().getId(), equalTo(bookingInputDto.getItemId()));
        assertThat(actualBookings.get(1).getBooker().getId(), equalTo(booker.getId()));
        assertThat(actualBookings.get(1).getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void getBookingsOwner() {
        User owner = new User(null, "User1", "u@email.com");
        em.persist(owner);

        Item item = new Item(null, "Item1", "GoodItem", true, owner, null);
        em.persist(item);

        User booker = new User(null, "Booker", "b@email.com");
        em.persist(booker);

        LocalDateTime start = LocalDateTime.now().minusHours(3);
        LocalDateTime end = LocalDateTime.now().plusHours(5);

        BookingInputDto bookingInputDto = makeBookingInputDto(item.getId(), start, end);
        BookingDto booking = service.create(bookingInputDto, booker.getId());

        LocalDateTime start2 = LocalDateTime.now().minusHours(1);
        LocalDateTime end2 = LocalDateTime.now().plusHours(3);

        BookingInputDto bookingInputDto2 = makeBookingInputDto(item.getId(), start2, end2);
        BookingDto booking2 = service.create(bookingInputDto2, booker.getId());

        List<BookingDto> actualBookings = service.getBookingsOwner("ALL", owner.getId(), 0, 10);

        assertThat(actualBookings, hasSize(2));
        assertThat(actualBookings.get(0).getId(), equalTo(booking2.getId()));
        assertThat(actualBookings.get(0).getStart(), equalTo(bookingInputDto2.getStart()));
        assertThat(actualBookings.get(0).getEnd(), equalTo(bookingInputDto2.getEnd()));
        assertThat(actualBookings.get(0).getItem().getId(), equalTo(bookingInputDto2.getItemId()));
        assertThat(actualBookings.get(0).getBooker().getId(), equalTo(booker.getId()));
        assertThat(actualBookings.get(0).getStatus(), equalTo(Status.WAITING));
        assertThat(actualBookings.get(1).getId(), equalTo(booking.getId()));
        assertThat(actualBookings.get(1).getStart(), equalTo(bookingInputDto.getStart()));
        assertThat(actualBookings.get(1).getEnd(), equalTo(bookingInputDto.getEnd()));
        assertThat(actualBookings.get(1).getItem().getId(), equalTo(bookingInputDto.getItemId()));
        assertThat(actualBookings.get(1).getBooker().getId(), equalTo(booker.getId()));
        assertThat(actualBookings.get(1).getStatus(), equalTo(Status.WAITING));
    }

    private BookingInputDto makeBookingInputDto(Long itemId, LocalDateTime start, LocalDateTime end) {
        BookingInputDto dto = new BookingInputDto();
        dto.setItemId(itemId);
        dto.setStart(start);
        dto.setEnd(end);

        return dto;
    }
}