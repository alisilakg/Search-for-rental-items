package ru.practicum.shareit.request;


import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(ItemRequestDto itemRequestInputDto, Long requesterId, LocalDateTime created);

    List<ItemRequestDto> getItemRequestsByRequesterId(Long userId);

    List<ItemRequestDto> getItemRequests(Long userId, int from, int size);

    ItemRequestDto getItemRequestById(Long requestId, Long userId);
}
