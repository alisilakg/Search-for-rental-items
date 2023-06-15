package ru.practicum.shareit.item;


import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item createItem(Item item, Long ownerId);

    Item getItemById(Long id);

    void deleteItemById(Long id);

    List <Item> getItemsBySearchQuery(String text);

    List<Item> getItemsByOwner(Long ownerId);

    Item updateItem(Item item);
}
