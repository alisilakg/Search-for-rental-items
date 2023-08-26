package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, PagingAndSortingRepository<Booking, Long> {
    List<Booking> findByBookerId(Long bookerId, Pageable page);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start,
                                                              LocalDateTime end, Pageable page);

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Pageable page);

    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime start, Pageable page);

    List<Booking> findByBookerIdAndStatus(Long bookerId, Status status, Pageable page);

    List<Booking> findByItemOwnerId(Long ownerId, Pageable page);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long ownerId, LocalDateTime start,
                                                                 LocalDateTime end, Pageable page);

    List<Booking> findByItemOwnerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Pageable page);

    List<Booking> findByItemOwnerIdAndStartIsAfter(Long bookerId, LocalDateTime start, Pageable page);

    List<Booking> findByItemOwnerIdAndStatus(Long bookerId, Status status, Pageable page);

    Booking findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(Long itemId, LocalDateTime start, Status status);

    Booking findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime end, Status status);

    Booking findFirstByItemIdAndBookerIdAndEndIsBeforeAndStatus(Long itemId, Long userId,
                                                                LocalDateTime end, Status status);

}
