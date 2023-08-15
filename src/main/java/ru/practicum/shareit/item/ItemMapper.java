package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAnswerRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

@Component
public class ItemMapper {
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public ItemMapper(BookingService bookingService, ItemService itemService, UserService userService, UserMapper userMapper) {
        this.bookingService = bookingService;
        this.itemService = itemService;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    public ItemDto toItemDtoForOwner(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId() != null ? item.getRequestId() : null,
                bookingService.getLastBooking(item.getId()),
                bookingService.getNextBooking(item.getId()),
                itemService.getCommentsByItemId(item.getId()));
    }

    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId() != null ? item.getRequestId() : null,
                null,
                null,
                itemService.getCommentsByItemId(item.getId()));
    }

    public Item toItem(ItemDto itemDto, Long ownerId) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                userMapper.toUser(userService.getUserById(ownerId)),
                itemDto.getRequestId() != null ? itemDto.getRequestId() : null);
    }

    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public ItemAnswerRequestDto toItemAnswerRequestDto(Item item) {
        return new ItemAnswerRequestDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId()
        );
    }

}
