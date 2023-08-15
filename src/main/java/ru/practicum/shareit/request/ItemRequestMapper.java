package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;

@Component
public class ItemRequestMapper {
    private final UserService userService;
    private final UserMapper userMapper;

    private final ItemService itemService;

    @Autowired
    public ItemRequestMapper(UserService userService, UserMapper userMapper, ItemService itemService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.itemService = itemService;
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequester(),
                itemRequest.getCreated(),
                itemService.getItemsByRequestId(itemRequest.getId()));
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestInputDto, Long requesterId, LocalDateTime created) {
        return new ItemRequest(
                null,
                itemRequestInputDto.getDescription(),
                userMapper.toUser(userService.getUserById(requesterId)),
                created);
    }
}
