package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.CheckService;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final BookingService bookingService;
    private final CheckService checker;

    @Autowired
    @Lazy
    public ItemServiceImpl(ItemMapper itemMapper,
                           CommentRepository commentRepository, ItemRepository itemRepository, BookingService bookingService, CheckService checkService) {
        this.itemMapper = itemMapper;
        this.commentRepository = commentRepository;
        this.itemRepository = itemRepository;
        this.bookingService = bookingService;
        this.checker = checkService;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        checker.isExistUser(ownerId);
        Item item = itemRepository.save(itemMapper.toItem(itemDto, ownerId));
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemById(Long id, Long userId) {
        ItemDto itemDto;
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с ID=" + id + " не найдена!"));
        if (userId.equals(item.getOwner().getId())) {
            itemDto = itemMapper.toItemDtoForOwner(item);
        } else {
            itemDto = itemMapper.toItemDto(item);
        }
        return itemDto;
    }

    @Override
    public void deleteItemById(Long id) {
        itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с ID=" + id + " не найдена!"));
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> getItemsBySearchQuery(String text) {
        if ((text != null) && (!text.isEmpty()) && (!text.isBlank())) {
            text = text.toLowerCase();
            return itemRepository.getItemsBySearchQuery(text).stream()
                    .map(itemMapper::toItemDto)
                    .collect(toList());
        } else return new ArrayList<>();
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(itemMapper::toItemDtoForOwner)
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(toList());
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long ownerId, Long itemId) {
        checker.isExistUser(ownerId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID=" + itemId + " не найдена!"));
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("У пользователя нет такой вещи!");
        }
        if (itemDto.getId() == null) {
            item.setId(itemId);
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        Comment comment = new Comment();
        Booking booking = bookingService.getBookingWithUserBookedItem(itemId, userId);
        if (booking != null) {
            comment.setCreated(LocalDateTime.now());
            comment.setItem(booking.getItem());
            comment.setAuthor(booking.getBooker());
            comment.setText(commentDto.getText());
        } else {
            throw new ValidationException("Данный пользователь вещь не бронировал!");
        }
        return itemMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getCommentsByItemId(Long itemId) {
        return commentRepository.findAllByItem_Id(itemId,
                        Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(itemMapper::toCommentDto)
                .collect(toList());
    }

    @Override
    public Item findItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с ID=" + id + " не найдена!"));
    }

}


