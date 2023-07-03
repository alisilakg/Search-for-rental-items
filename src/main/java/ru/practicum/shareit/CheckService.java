package ru.practicum.shareit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;



@Service
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
        return itemService.getItemsByOwner(userId).stream()
                .anyMatch(i -> i.getId().equals(itemId));
    }

}
