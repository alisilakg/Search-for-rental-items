package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAnswerRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto getItemById(Long id, Long ownerId);

    ItemDto createItem(ItemDto itemDto, Long ownerId);

    List<ItemDto> getItemsByOwner(Long ownerId, int from, int size);
    void deleteItemById(Long itemId, Long userId);

    List<ItemDto> getItemsBySearchQuery(String text, int from, int size);

    ItemDto updateItem(ItemDto itemDto, Long ownerId, Long itemId);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long userId);

    List<CommentDto> getCommentsByItemId(Long itemId);

    Item findItemById(Long id);

    List<ItemAnswerRequestDto> getItemsByRequestId(Long id);

}
