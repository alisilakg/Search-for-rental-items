package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto getItemById(Long id, Long ownerId);

    ItemDto createItem(ItemDto itemDto, Long ownerId);

    List<ItemDto> getItemsByOwner(Long ownerId);

    void deleteItemById(Long itemId);

    List<ItemDto> getItemsBySearchQuery(String text);

    ItemDto updateItem(ItemDto itemDto, Long ownerId, Long itemId);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long userId);

    List<CommentDto> getCommentsByItemId(Long itemId);

    Item findItemById(Long id);
}
