package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingTimeDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class ItemServiceIntegrationTest {
    private final EntityManager em;
    private final ItemService itemService;

    @Test
    void getItemById() {
        User owner = new User(null, "User1", "u@email.com");
        em.persist(owner);

        Boolean available = true;
        Item item = new Item(null, "Item1", "GoodItem", available, owner, null);
        em.persist(item);

        User booker = new User(null, "Booker", "b@email.com");
        em.persist(booker);

        ItemDto actualItem = itemService.getItemById(item.getId(), booker.getId());

        assertThat(actualItem.getId(), notNullValue());
        assertThat(actualItem.getName(), equalTo(item.getName()));
        assertThat(actualItem.getDescription(), equalTo(item.getDescription()));
        assertThat(actualItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(actualItem.getLastBooking(), nullValue());
        assertThat(actualItem.getNextBooking(), nullValue());
    }


    @Test
    void deleteItemById() {
        User owner = new User(null, "User1", "u@email.com");
        em.persist(owner);

        Boolean available = true;
        Item item = new Item(null, "Item1", "GoodItem", available, owner, null);
        em.persist(item);

        User booker = new User(null, "Booker", "b@email.com");
        em.persist(booker);

        itemService.deleteItemById(item.getId(), owner.getId());

        String error = String.format("Вещь с ID=" + item.getId() + " не найдена!");
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.getItemById(item.getId(), owner.getId())
        );

        assertEquals(error, exception.getMessage());
    }

    @Test
    void getItemsBySearchQuery() {
        User owner = new User(null, "User1", "u@email.com");
        em.persist(owner);

        Boolean available = true;
        Item item = new Item(null, "Электродрель", "Good", available, owner, null);
        em.persist(item);

        Item item2 = new Item(null, "Отвёртка", "Good", available, owner, null);
        em.persist(item2);

        Item item3 = new Item(null, "Просто дрель", "Good", available, owner, null);
        em.persist(item3);

        User booker = new User(null, "Booker", "b@email.com");
        em.persist(booker);

        List<ItemDto> actualItems = itemService.getItemsBySearchQuery("ДреЛь", 0, 10);

        assertEquals(actualItems.size(), 2);
        assertThat(actualItems.get(0).getId(), notNullValue());
        assertThat(actualItems.get(0).getName(), equalTo(item.getName()));
        assertThat(actualItems.get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(actualItems.get(0).getAvailable(), equalTo(item.getAvailable()));
        assertThat(actualItems.get(0).getLastBooking(), nullValue());
        assertThat(actualItems.get(0).getNextBooking(), nullValue());
        assertThat(actualItems.get(1).getId(), notNullValue());
        assertThat(actualItems.get(1).getName(), equalTo(item3.getName()));
        assertThat(actualItems.get(1).getDescription(), equalTo(item3.getDescription()));
        assertThat(actualItems.get(1).getAvailable(), equalTo(item3.getAvailable()));
        assertThat(actualItems.get(1).getLastBooking(), nullValue());
        assertThat(actualItems.get(1).getNextBooking(), nullValue());
    }


    @Test
    void updateItem() {
        User owner = new User(null, "User1", "u@email.com");
        em.persist(owner);

        Boolean available = true;
        Item item = new Item(null, "Электродрель", "Good", available, owner, null);
        em.persist(item);

        User booker = new User(null, "Booker", "b@email.com");
        em.persist(booker);

        LocalDateTime startNext = LocalDateTime.now().plusHours(1);
        LocalDateTime endNext = LocalDateTime.now().plusHours(2);
        Booking bookingNext = new Booking(null, startNext,
                endNext, item, booker, Status.APPROVED);
        em.persist(bookingNext);
        BookingTimeDto bookingTimeDtoNext = makeBookingTimeDto(bookingNext.getId(), bookingNext.getBooker().getId(),
                bookingNext.getStart(), bookingNext.getEnd());

        LocalDateTime startLast = LocalDateTime.now().minusHours(2);
        LocalDateTime endLast = LocalDateTime.now().minusHours(1);
        Booking bookingLast = new Booking(null, startLast,
                endLast, item, booker, Status.APPROVED);
        em.persist(bookingLast);
        BookingTimeDto bookingTimeDtoLast = makeBookingTimeDto(bookingLast.getId(), bookingLast.getBooker().getId(),
                bookingLast.getStart(), bookingLast.getEnd());

        ItemDto itemToUpdate = makeItemDto("Просто дрель", "GoodGood", available, 0L,
                bookingTimeDtoLast, bookingTimeDtoNext, null);

        ItemDto actualItem = itemService.updateItem(itemToUpdate, owner.getId(), item.getId());

        assertThat(actualItem.getId(), notNullValue());
        assertThat(actualItem.getName(), equalTo(itemToUpdate.getName()));
        assertThat(actualItem.getDescription(), equalTo(itemToUpdate.getDescription()));
        assertThat(actualItem.getAvailable(), equalTo(itemToUpdate.getAvailable()));
        assertThat(actualItem.getLastBooking(), nullValue());
        assertThat(actualItem.getNextBooking(), nullValue());
    }


    @Test
    void createComment() {
        User owner = new User(null, "User1", "u@email.com");
        em.persist(owner);

        Boolean available = true;
        Item item = new Item(null, "Электродрель", "Good", available, owner, null);
        em.persist(item);

        User booker = new User(null, "Booker", "b@email.com");
        em.persist(booker);

        LocalDateTime start = LocalDateTime.now().minusHours(2);
        LocalDateTime end = LocalDateTime.now().minusHours(1);
        Booking booking = new Booking(null, start, end, item, booker, Status.APPROVED);
        em.persist(booking);

        CommentDto commentDto = makeCommentDto("Очень хорошая вещь", item, booker.getName(), null);

        CommentDto actualComment = itemService.createComment(commentDto, item.getId(), booker.getId());

        assertThat(actualComment.getId(), notNullValue());
        assertThat(actualComment.getText(), equalTo(commentDto.getText()));
        assertThat(actualComment.getItem().getId(), equalTo(item.getId()));
        assertThat(actualComment.getItem().getName(), equalTo(item.getName()));
        assertThat(actualComment.getItem().getDescription(), equalTo(item.getDescription()));
        assertThat(actualComment.getAuthorName(), equalTo(booker.getName()));
        assertThat(actualComment.getCreated(), notNullValue());
    }

    private CommentDto makeCommentDto(String text, Item item, String authorName, LocalDateTime created) {
        CommentDto dto = new CommentDto();
        dto.setText(text);
        dto.setItem(item);
        dto.setAuthorName(authorName);
        dto.setCreated(created);

        return dto;
    }


    private BookingTimeDto makeBookingTimeDto(long id, long bookerId, LocalDateTime start, LocalDateTime end) {
        BookingTimeDto dto = new BookingTimeDto();
        dto.setId(id);
        dto.setBookerId(bookerId);
        dto.setStart(start);
        dto.setEnd(end);

        return dto;
    }

    private ItemDto makeItemDto(String name, String description, Boolean available, long requestId,
                                BookingTimeDto bookingLast, BookingTimeDto bookingNext, List<CommentDto> comments) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        dto.setRequestId(requestId);
        dto.setLastBooking(bookingLast);
        dto.setNextBooking(bookingNext);
        dto.setComments(comments);

        return dto;
    }
}
