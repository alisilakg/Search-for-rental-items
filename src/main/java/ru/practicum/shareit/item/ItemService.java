package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ItemService {
    private final ItemStorage itemStorage;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemService(ItemStorage itemStorage, ItemMapper itemMapper) {
        this.itemStorage = itemStorage;
        this.itemMapper = itemMapper;
    }

    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        return itemMapper.toItemDto(itemStorage.createItem(itemMapper.toItem(itemDto, ownerId), ownerId));
    }

    public ItemDto getItemById(Long id) {
        return itemMapper.toItemDto(itemStorage.getItemById(id));
    }

    public void deleteItemById(Long id) {
        itemStorage.deleteItemById(id);
    }

    public List<ItemDto> getItemsBySearchQuery(String text) {
        text = text.toLowerCase();
        return itemStorage.getItemsBySearchQuery(text).stream()
                .map(itemMapper::toItemDto)
                .collect(toList());
    }

    public List<ItemDto> getItemsByOwner(Long ownerId) {
        return itemStorage.getItemsByOwner(ownerId).stream()
                .map(itemMapper::toItemDto)
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
        return itemMapper.toItemDto(itemStorage.updateItem(itemMapper.toItem(itemDto, ownerId)));
    }
}


