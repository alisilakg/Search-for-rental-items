package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Component
@Repository
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private Long counterIdItem = 1L;

    public Long generateIdItem() {
        return counterIdItem++;
    }

    @Override
    public Item createItem(Item item, Long ownerId) {
        item.setId(generateIdItem());
        item.setOwnerId(ownerId);
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item getItemById(Long id) {
        if (!items.containsKey(id)) {
            throw new NotFoundException(String.format("Вещи с id %d нет.", id));
        }
        return items.get(id);
    }

    @Override
    public void deleteItemById(Long id) {
        if (!items.containsKey(id)) {
            throw new NotFoundException(String.format("Вещи с id %d нет.", id));
        }
        items.remove(id);
    }

    @Override
    public List<Item> getItemsBySearchQuery(String text) {
        List<Item> searchItems = new ArrayList<>();
        if (!text.isBlank()) {
            searchItems = items.values().stream()
                    .filter(item -> item.getAvailable())
                    .filter(item -> item.getName().toLowerCase().contains(text) ||
                            item.getDescription().toLowerCase().contains(text))
                    .collect(toList());
        }
        return searchItems;
    }

    @Override
    public List<Item> getItemsByOwner(Long ownerId) {
        return new ArrayList<>(items.values().stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .collect(toList()));
    }

    @Override
    public Item updateItem(Item item) {
        if (!items.containsKey(item.getId())) {
            throw new NotFoundException(String.format("Вещи с id %d нет.", item.getId()));
        }
        if (item.getName() == null) {
            item.setName(items.get(item.getId()).getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(items.get(item.getId()).getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(items.get(item.getId()).getAvailable());
        }
        items.put(item.getId(), item);
        return item;
    }

}
