package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;

@Service
@Slf4j
public class CheckService {
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public CheckService(UserService userService, ItemService itemService) {
        this.userService = userService;
        this.itemService = itemService;
    }

    public boolean isExistUser(Long userId) {
        boolean exist = false;
        if (userService.getUserById(userId) != null) {
            exist = true;
        }
        return exist;
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
