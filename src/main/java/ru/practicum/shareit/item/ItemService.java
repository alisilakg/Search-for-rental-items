package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.item.ItemMapper.toItem;
import static ru.practicum.shareit.item.ItemMapper.toItemDto;

@Service
public class ItemService {
    private final ItemStorage itemStorage;

    @Autowired
    public ItemService(ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }

    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        return toItemDto(itemStorage.createItem(toItem(itemDto, ownerId), ownerId));
    }

    public ItemDto getItemById(Long id) {
        return toItemDto(itemStorage.getItemById(id));
    }

    public void deleteItemById(Long id) {
        itemStorage.deleteItemById(id);
    }

    public List<ItemDto> getItemsBySearchQuery(String text) {
        text = text.toLowerCase();
        return itemStorage.getItemsBySearchQuery(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    public List<ItemDto> getItemsByOwner(Long ownerId) {
        return itemStorage.getItemsByOwner(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    public ItemDto updateItem(ItemDto itemDto, Long ownerId, Long itemId) {
        if (itemDto.getId() == null) {
            itemDto.setId(itemId);
        }
        Item oldItem = itemStorage.getItemById(itemId);
        if (!oldItem.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("У пользователя нет такой вещи!");
        }
        return toItemDto(itemStorage.updateItem(toItem(itemDto, ownerId)));
    }
}


