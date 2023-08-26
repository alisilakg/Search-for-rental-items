package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.exception.NotFoundException;

@Service
@Slf4j
public class CheckService {
    private final UserService userService;
    private final ItemService itemService;

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public CheckService(UserService userService, ItemService itemService, UserRepository userRepository,
                        ItemRepository itemRepository) {
        this.userService = userService;
        this.itemService = itemService;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    public boolean isExistUser(Long userId) {
        boolean exist = false;
        if (userService.getUserById(userId) != null) {
            exist = true;
        }
        return exist;
    }

    public User checkUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id = " + userId + " не зарегестрирован"));
    }

    public Item checkItem(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с id = " + itemId + " не зарегестрирована"));
    }

    public boolean isAvailableItem(BookingInputDto bookingInputDto) {
        return itemService.findItemById(bookingInputDto.getItemId()).getAvailable();
    }

    public boolean isItemOwner(Long itemId, Long userId) {
        return itemService.getItemsByOwner(userId, 0, 10).stream()
                .anyMatch(i -> i.getId().equals(itemId));
    }

    public boolean isItemBooker(Booking booking, Long userId) {
        return booking.getBooker().getId().equals(userId);
    }

}
