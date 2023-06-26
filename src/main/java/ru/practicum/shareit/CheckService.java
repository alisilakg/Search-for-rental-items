package ru.practicum.shareit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;



@Service
public class CheckService {
    private UserService userService;
    private ItemService itemService;
    private BookingServiceImpl bookingService;

    @Autowired
    public CheckService(UserService userService, ItemService itemService,
                                   BookingServiceImpl bookingService) {
        this.userService = userService;
        this.itemService = itemService;
        this.bookingService = bookingService;
    }

    public boolean isExistUser(Long userId) {
        boolean exist = false;
        if (userService.getUserById(userId) != null) {
            exist = true;
        }
        return exist;
    }

    public boolean isAvailableItem(Long itemId) {
        return itemService.findItemById(itemId).getAvailable();
    }

    public boolean isItemOwner(Long itemId, Long userId) {
        return itemService.getItemsByOwner(userId).stream()
                .anyMatch(i -> i.getId().equals(itemId));
    }
//
//    public Booking getBookingWithUserBookedItem(Long itemId, Long userId) {
//        return bookingService.getBookingWithUserBookedItem(itemId, userId);
//    }
}
