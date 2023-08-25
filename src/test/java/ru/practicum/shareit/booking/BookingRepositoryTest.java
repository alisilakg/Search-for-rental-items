package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    TestEntityManager em;

    @Autowired
    private BookingRepository repository;

    private static final Sort SORT = Sort.by(Sort.Direction.DESC, "start");

    private final int size = 30;

    private final PageRequest page = PageRequest.of(0, size, SORT);

    private final User owner = new User(
            null,
            "Owner",
            "o@mail.ru");

    private final Item item = new Item(
            null,
            "Кусторез",
            "Бывалый",
            true,
            owner,
            null);

    private final User booker = new User(
            null,
            "Booker",
            "a@mail.ru");

    private final Status status = Status.APPROVED;

    private final Booking booking = new Booking(
            null,
            LocalDateTime.now().plusHours(2),
            LocalDateTime.now().plusHours(1),
            item,
            booker,
            status);

    @BeforeEach
    void setUp() {
        em.persist(owner);
        em.persist(item);
        em.persist(booker);
        em.persist(booking);
    }

    @Test
    void findByBookerId_thenReturnBookingList() {
        List<Booking> actualList = repository.findByBookerId(booker.getId(), page);

        assertNotNull(actualList);
        assertEquals(booking.getItem().getId(), actualList.get(0).getItem().getId());
    }

    @Test
    void findByItemOwnerId_thenReturnBookingList() {
        List<Booking> actualList = repository.findByItemOwnerId(owner.getId(), page);

        assertNotNull(actualList);
        assertEquals(booking.getItem().getId(), actualList.get(0).getItem().getId());
    }

    @Test
    void findByBookerIdAndStartIsBeforeAndEndIsAfter_thenReturnBooringList_thenReturnBookingList() {
        List<Booking> actualList = repository.findByBookerIdAndStartIsBeforeAndEndIsAfter(booker.getId(),
                LocalDateTime.now().plusHours(3), LocalDateTime.now().plusMinutes(20), page);

        assertNotNull(actualList);
        assertEquals(booking.getItem().getId(), actualList.get(0).getItem().getId());
    }

    @Test
    void findByItemOwnerIdAndStartIsBeforeAndEndIsAfter_thenReturnBookingList() {
        List<Booking> actualList = repository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(owner.getId(),
                LocalDateTime.now().plusHours(3), LocalDateTime.now().plusMinutes(20), page);

        assertNotNull(actualList);
        assertEquals(booking.getItem().getId(), actualList.get(0).getItem().getId());
    }

    @Test
    void findByBookerIdAndEndIsBefore_thenReturnBookingList() {
        List<Booking> actualList = repository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(owner.getId(),
                LocalDateTime.now().plusHours(3), LocalDateTime.now().plusMinutes(20), page);

        assertNotNull(actualList);
        assertEquals(booking.getItem().getId(), actualList.get(0).getItem().getId());
    }

    @Test
    void findByItemOwnerIdAndEndIsBefore_thenReturnBookingList() {
        List<Booking> actualList = repository.findByItemOwnerIdAndEndIsBefore(owner.getId(),
                LocalDateTime.now().plusHours(2), page);

        assertNotNull(actualList);
        assertEquals(booking.getItem().getId(), actualList.get(0).getItem().getId());
    }

    @Test
    void findByBookerIdAndStartIsAfter_thenReturnBookingList() {
        List<Booking> actualList = repository.findByBookerIdAndStartIsAfter(booker.getId(),
                LocalDateTime.now().plusHours(1), page);

        assertNotNull(actualList);
        assertEquals(booking.getItem().getId(), actualList.get(0).getItem().getId());
    }

    @Test
    void findByItemOwnerIdAndStartIsAfter_thenReturnBookingList() {
        List<Booking> actualList = repository.findByItemOwnerIdAndStartIsAfter(owner.getId(),
                LocalDateTime.now().plusHours(1), page);

        assertNotNull(actualList);
        assertEquals(booking.getItem().getId(), actualList.get(0).getItem().getId());
    }

    @Test
    void findByBookerIdAndStatus_thenReturnBookingList() {
        List<Booking> actualList = repository.findByBookerIdAndStatus(booker.getId(),
                status, page);

        assertNotNull(actualList);
        assertEquals(booking.getItem().getId(), actualList.get(0).getItem().getId());
    }

    @Test
    void findByItemOwnerIdAndStatus_thenReturnBookingList() {
        List<Booking> actualList = repository.findByItemOwnerIdAndStatus(owner.getId(),
                status, page);

        assertNotNull(actualList);
        assertEquals(booking.getItem().getId(), actualList.get(0).getItem().getId());
    }

    @Test
    void findFirstByItem_IdAndStartBeforeAndStatus_thenReturnBooking() {
        Booking actualBooking = repository.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(item.getId(),
                LocalDateTime.now().plusHours(3), status);

        assertNotNull(actualBooking);
        assertEquals(booking.getItem().getId(), actualBooking.getItem().getId());
    }

    @Test
    void findFirstByItem_IdAndStartAfterAndStatus_thenReturnBooking() {
        Booking actualBooking = repository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(item.getId(),
                LocalDateTime.now().plusHours(1), status);

        assertNotNull(actualBooking);
        assertEquals(booking.getItem().getId(), actualBooking.getItem().getId());
    }

    @Test
    void findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus_thenReturnBooking() {
        Booking actualBooking = repository.findFirstByItemIdAndBookerIdAndEndIsBeforeAndStatus(item.getId(),
                booker.getId(), LocalDateTime.now().plusHours(3), status);

        assertNotNull(actualBooking);
        assertEquals(booking.getItem().getId(), actualBooking.getItem().getId());
    }
}