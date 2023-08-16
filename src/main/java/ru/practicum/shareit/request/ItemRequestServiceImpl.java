package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.CheckService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@Validated
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final CheckService checker;
    private final ItemRequestMapper itemRequestMapper;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, CheckService checker,
                                  ItemRequestMapper itemRequestMapper) {
        this.itemRequestRepository = itemRequestRepository;
        this.checker = checker;
        this.itemRequestMapper = itemRequestMapper;
    }

    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestInputDto, Long requesterId, LocalDateTime created) {
        checker.isExistUser(requesterId);
        isValidRequest(itemRequestInputDto);
        ItemRequest itemRequest = itemRequestRepository.save(itemRequestMapper.toItemRequest(itemRequestInputDto,
                requesterId, created));
        return itemRequestMapper.toItemRequestDto(itemRequest);
    }

    void isValidRequest(ItemRequestDto itemRequestInputDto) {
        if (itemRequestInputDto.getDescription() == null || itemRequestInputDto.getDescription().isBlank()) {
            log.error("ItemRequest description empty");
            throw new ValidationException("Описание запроса вещи не может быть пустым.");
        }
    }

    @Override
    public List<ItemRequestDto> getItemRequestsByRequesterId(Long userId) {
        checker.isExistUser(userId);
        List<ItemRequestDto> itemRequestDtos = itemRequestRepository.findByRequesterId(userId).stream()
                .map(itemRequestMapper::toItemRequestDto)
                .sorted(Comparator.comparing(ItemRequestDto::getId))
                .collect(toList());
        return itemRequestDtos;
    }


    @Override
    public List<ItemRequestDto> getItemRequests(Long userId, int from, int size) {
        checker.isExistUser(userId);
        PageRequest page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        return itemRequestRepository.findByRequesterIdNot(userId, page).stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(toList());
    }

    @Override
    public ItemRequestDto getItemRequestById(Long requestId, Long userId) {
        checker.isExistUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запроса с ID=" + requestId + " не найдено!"));
        return itemRequestMapper.toItemRequestDto(itemRequest);
    }
}
