package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.CheckService;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingTimeDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAnswerRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository mockItemRepository;

    @Mock
    private CommentRepository mockCommentRepository;

    @Mock
    private ItemMapper mockItemMapper;
    @Mock
    private CheckService mockCheckService;

    @Mock
    private BookingServiceImpl mockBookingService;
    @InjectMocks
    private ItemServiceImpl itemService;
    private User owner;
    private Item item;
    private ItemDto itemToSave;

    private ItemDto itemForOwner;

    private BookingTimeDto lastBooking;

    private BookingTimeDto nextBooking;

    private List<CommentDto> comments = new ArrayList<>();

    private CommentDto comment;

    private ItemDto newItem;

    private User booker;

    private Booking booking;

    private ItemAnswerRequestDto itemAnswerRequest;

    @BeforeEach
    void setup() {
        owner = new User();
        owner.setName("name");
        owner.setEmail("e@mail.ru");
        owner.setId(1L);

        item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequestId(1L);

        itemToSave = new ItemDto();
        itemToSave.setId(1L);
        itemToSave.setName("name");
        itemToSave.setDescription("description");
        itemToSave.setAvailable(true);
        itemToSave.setRequestId(1L);

        lastBooking = new BookingTimeDto();
        lastBooking.setId(1L);
        lastBooking.setBookerId(1L);
        lastBooking.setStart(LocalDateTime.of(2023,1,1,1,1));
        lastBooking.setEnd(LocalDateTime.of(2023,1,3,1,1));

        nextBooking = new BookingTimeDto();
        nextBooking.setId(2L);
        nextBooking.setBookerId(2L);
        nextBooking.setStart(LocalDateTime.now().plusHours(2));
        nextBooking.setEnd(LocalDateTime.now().plusHours(4));

        comment = new CommentDto();
        comment.setId(1L);
        comment.setText("good item");
        comment.setItem(item);
        comment.setAuthorName("Ivanov");
        comment.setCreated(LocalDateTime.now());

        comments.add(comment);

        itemForOwner = new ItemDto();
        itemForOwner.setId(1L);
        itemForOwner.setName("name");
        itemForOwner.setDescription("description");
        itemForOwner.setAvailable(true);
        itemForOwner.setRequestId(1L);
        itemForOwner.setLastBooking(lastBooking);
        itemForOwner.setNextBooking(nextBooking);
        itemForOwner.setComments(comments);

        newItem = new ItemDto();
        newItem.setId(null);
        newItem.setName("newName");
        newItem.setDescription("newDescription");
        newItem.setAvailable(true);
        newItem.setRequestId(1L);

        booker = new User();
        booker.setName("nameBooker");
        booker.setEmail("booker@mail.ru");
        booker.setId(2L);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2023,1,1,1,1));
        booking.setEnd(LocalDateTime.of(2023,1,3,1,1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.APPROVED);

        itemAnswerRequest = new ItemAnswerRequestDto();
        itemAnswerRequest.setId(1L);
        itemAnswerRequest.setName("name");
        itemAnswerRequest.setDescription("description");
        itemAnswerRequest.setAvailable(true);
        itemAnswerRequest.setRequestId(1L);
    }

    @Test
    void createItem_whenItemValid_thenSaveItem() {
        when(mockCheckService.isExistUser(owner.getId())).thenReturn(true);
        when(mockItemMapper.toItem(itemToSave, owner.getId())).thenReturn(item);
        when(mockItemMapper.toItemDto(item)).thenReturn(itemToSave);
        when(mockItemRepository.save(any())).thenReturn(item);

        ItemDto actualItemDto = itemService.createItem(itemToSave,
                owner.getId());

        assertNotNull(actualItemDto);
        assertEquals(item.getId(), actualItemDto.getId());
        assertThat(actualItemDto.getDescription(), equalTo(itemToSave.getDescription()));
        verify(mockItemRepository, times(1)).save(any());
    }

    @Test
    void createItem_whenOwnerNotFound_thenReturnedNotFoundExceptionThrown() {
        long userNotFoundId = 0L;
        String error = String.format("Пользователь с ID=" + userNotFoundId + " не найден!");
        when(mockCheckService.isExistUser(userNotFoundId)).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.createItem(itemToSave, userNotFoundId)
        );

        assertEquals(error, exception.getMessage());
        verify(mockItemRepository, times(0)).save(any());
    }

    @Test
    void createItem_whenNameIsEmpty_thenReturnedValidationExceptionThrown() {
        String error = "Название вещи не может быть пустым.";
        itemToSave.setName("");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.createItem(itemToSave, owner.getId())
        );

        assertEquals(error, exception.getMessage());
        verify(mockItemRepository, times(0)).save(any());
    }

    @Test
    void createItem_whenAvailableIsEmpty_thenReturnedValidationExceptionThrown() {
        String error = "Доступность вещи не может быть пустой.";
        itemToSave.setAvailable(null);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.createItem(itemToSave, owner.getId())
        );

        assertEquals(error, exception.getMessage());
        verify(mockItemRepository, times(0)).save(any());
    }

    @Test
    void createItem_whenDescriptionIsEmpty_thenReturnedValidationExceptionThrown() {
        String error = "Описание вещи не может быть пустым.";
        itemToSave.setDescription("");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.createItem(itemToSave, owner.getId())
        );

        assertEquals(error, exception.getMessage());
        verify(mockItemRepository, times(0)).save(any());
    }

    @Test
    void getItemById_whenItemFoundAndUserIsOwner_thenReturnedItemForOwner() {
        long userId = owner.getId();
        long itemId = item.getId();
        when(mockItemMapper.toItemDtoForOwner(item)).thenReturn(itemToSave);
        when(mockItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(mockCheckService.isItemOwner(itemId, userId)).thenReturn(true);

        ItemDto actualItem = itemService.getItemById(itemId, userId);

        assertThat(actualItem.getName(), equalTo(itemToSave.getName()));
        assertThat(actualItem.getDescription(), equalTo(itemToSave.getDescription()));
        assertThat(actualItem.getAvailable(), equalTo(itemToSave.getAvailable()));
        assertThat(actualItem.getRequestId(), equalTo(itemToSave.getRequestId()));
    }

    @Test
    void getItemById_whenItemFound_thenReturnedItem() {
        User user = new User();
        user.setName("name2");
        user.setEmail("e2@mail.ru");
        user.setId(2L);
        long userId = user.getId();
        long itemId = item.getId();
        when(mockItemMapper.toItemDto(item)).thenReturn(itemToSave);
        when(mockItemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemDto actualItem = itemService.getItemById(itemId, userId);

        assertThat(actualItem.getName(), equalTo(itemToSave.getName()));
        assertThat(actualItem.getDescription(), equalTo(itemToSave.getDescription()));
        assertThat(actualItem.getAvailable(), equalTo(itemToSave.getAvailable()));
        assertThat(actualItem.getRequestId(), equalTo(itemToSave.getRequestId()));
    }

    @Test
    void getItemById_whenItemNotFound_thenNotFoundExceptionThrown() {
        long itemNotFoundId = 0L;
        String error = String.format("Вещь с ID=" + itemNotFoundId + " не найдена!");
        when(mockItemRepository.findById(itemNotFoundId)).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.getItemById(itemNotFoundId, any())
        );

        assertEquals(error, exception.getMessage());
    }

    @Test
    void deleteItemById_whenItemNotFound_thenNotFoundExceptionThrown() {
        long userId = owner.getId();
        long itemId = 2L;
        String error = String.format("Вещь с ID=" + itemId + " не найдена!");
        when(mockCheckService.isItemOwner(itemId, userId)).thenReturn(true);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.deleteItemById(itemId, userId));

        assertEquals(error, exception.getMessage());
    }


    @Test
    void deleteItemById_whenItemFound_thenDeleteItem() {
        long userId = owner.getId();
        long itemId = item.getId();
        when(mockCheckService.isItemOwner(itemId, userId)).thenReturn(true);
        when(mockItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        doNothing().when(mockItemRepository).deleteById(itemId);

        itemService.deleteItemById(userId, itemId);

        verify(mockItemRepository, times(1)).deleteById(any());
    }


    @Test
    void deleteItemById_whenUserNotOwner_thenValidationExceptionThrown() {
        long notOwnerId = 2L;
        long itemId = item.getId();
        String error = "Удалить вещь может только владелец!";
        when(mockCheckService.isItemOwner(itemId, notOwnerId)).thenReturn(false);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.deleteItemById(itemId, notOwnerId));

        assertEquals(error, exception.getMessage());
    }

    @Test
    void getItemsBySearchQuery_whenTextIsEmpty_thenReturnedEmptyList() {
        int from = 0;
        int size = 1;
        String text = "";

        List<ItemDto> actualItems = itemService.getItemsBySearchQuery(text, from, size);

        assertNotNull(actualItems);
        assertEquals(0, actualItems.size());
    }

    @Test
    void getItemsBySearchQuery_whenTextIsNotEmpty_thenReturnedItem() {
        int from = 0;
        int size = 1;
        String text = "nAmE";
        when(mockItemRepository.getItemsBySearchQuery(any(), any())).thenReturn(List.of(item));
        when(mockItemMapper.toItemDto(item)).thenReturn(itemToSave);

        List<ItemDto> actualItems = itemService.getItemsBySearchQuery(text, from, size);

        assertNotNull(actualItems);
        assertEquals(1, actualItems.size());
        assertEquals(item.getId(), actualItems.get(0).getId());
    }

    @Test
    void getItemsByOwner_whenUserNotHaveItems_thenReturnedEmptyList() {
        long userId = owner.getId();
        int from = 0;
        int size = 1;
        PageRequest page = PageRequest.of(from / size, size);
        when(mockCheckService.isExistUser(owner.getId())).thenReturn(true);
        when(mockItemRepository.findByOwnerId(userId, page)).thenReturn(emptyList());

        List<ItemDto> actualItems = itemService.getItemsByOwner(userId, from, size);

        assertNotNull(actualItems);
        assertEquals(0, actualItems.size());
}

    @Test
    void getItemsByOwner_whenUserHaveOneItem_thenReturnedOneItem() {
        long userId = owner.getId();
        int from = 0;
        int size = 1;
        PageRequest page = PageRequest.of(from / size, size);
        when(mockCheckService.isExistUser(owner.getId())).thenReturn(true);
        when(mockItemMapper.toItemDtoForOwner(item)).thenReturn(itemForOwner);
        when(mockItemRepository.findByOwnerId(userId, page)).thenReturn(List.of(item));

        List<ItemDto> actualItems = itemService.getItemsByOwner(userId, from, size);

        assertNotNull(actualItems);
        assertEquals(1, actualItems.size());
        assertEquals(lastBooking.getId(), actualItems.get(0).getLastBooking().getId());

    }

    @Test
    void updateItem_regularCase() {
        long userId = 1L;
        long itemId = 1L;
        String newName = "nameUpdate";
        String newDescription = "newDescription";
        item.setName(newName);
        item.setDescription(newDescription);
        when(mockItemRepository.save(any())).thenReturn(item);
        when(mockItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(mockItemMapper.toItemDto(any())).thenReturn(newItem);

        ItemDto actualItem = itemService.updateItem(newItem, userId, itemId);

        assertNotNull(actualItem);
        assertEquals("newName", actualItem.getName());
        assertEquals("newDescription", actualItem.getDescription());
        assertEquals(true, actualItem.getAvailable());
        verify(mockItemRepository, times(1)).save(any());
    }


    @Test
    void updateItem_whenUserNotFound_thenNotFoundExceptionThrow() {
        long userNotFoundId = 0L;
        String error = String.format("Пользователь с ID=" + userNotFoundId + " не найден!");
        when(mockCheckService.isExistUser(userNotFoundId)).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(itemToSave, userNotFoundId, anyLong())
        );

        assertEquals(error, exception.getMessage());
        verify(mockItemRepository, times(0)).save(any());
    }

    @Test
    void updateItem_whenItemNotFound_thenNotFoundExceptionThrow() {
        long itemNotFoundId = 0L;
        String error = String.format("Вещь с ID=" + itemNotFoundId + " не найдена!");
        when(mockItemRepository.findById(itemNotFoundId)).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(itemToSave, anyLong(), itemNotFoundId)
        );

        assertEquals(error, exception.getMessage());
        verify(mockItemRepository, times(0)).save(any());
    }

    @Test
    void updateItem_whenUserNotHaveThisItem_thenNotFoundExceptionThrow() {
        long userId = 2L;
        long itemId = 1L;
        String error = "У пользователя нет такой вещи!";
        when(mockCheckService.isExistUser(userId)).thenReturn(true);
        when(mockItemRepository.findById(itemId)).thenReturn(Optional.of(item));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(itemToSave, userId, item.getId())
        );

        assertEquals(error, exception.getMessage());
        verify(mockItemRepository, times(0)).save(any());
    }

    @Test
    void updateItem_whenNewItemNameIsEmpty_thenReturnedItemWithLastName() {
        long userId = 1L;
        long itemId = 1L;
        newItem.setName(null);
        when(mockCheckService.isExistUser(userId)).thenReturn(true);
        when(mockItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        itemToSave.setDescription("descriptionNew");
        when(mockItemMapper.toItemDto(any())).thenReturn(itemToSave);

        ItemDto actualItem = itemService.updateItem(newItem, userId, itemId);

        assertNotNull(actualItem);
        assertEquals(item.getId(), actualItem.getId());
        assertEquals(item.getName(), actualItem.getName());
        verify(mockItemRepository, times(1)).save(any());
    }

    @Test
    void updateItem_whenNewItemDescriptionIsEmpty_thenReturnedItemWithLastDescription() {
        long userId = 1L;
        long itemId = 1L;
        newItem.setDescription(null);
        when(mockCheckService.isExistUser(userId)).thenReturn(true);
        when(mockItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        itemToSave.setName("newName");
        when(mockItemMapper.toItemDto(any())).thenReturn(itemToSave);

        ItemDto actualItem = itemService.updateItem(newItem, userId, itemId);

        assertNotNull(actualItem);
        assertEquals(item.getId(), actualItem.getId());
        assertEquals(item.getDescription(), actualItem.getDescription());
        verify(mockItemRepository, times(1)).save(any());
    }


    @Test
    void updateItem_whenNewItemIdIsEmpty_thenReturnedItemWithLastId() {
        long userId = 1L;
        long itemId = 1L;
        when(mockCheckService.isExistUser(userId)).thenReturn(true);
        when(mockItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        itemToSave.setName("newName");
        itemToSave.setDescription("newDescription");
        when(mockItemMapper.toItemDto(any())).thenReturn(itemToSave);

        ItemDto actualItem = itemService.updateItem(newItem, userId, itemId);

        assertNotNull(actualItem);
        assertEquals(item.getId(), actualItem.getId());
        verify(mockItemRepository, times(1)).save(any());
    }

    @Test
    void createComment_whenUserIsNotBooker_thenValidationExceptionThrow() {
        long userId = 2L;
        long itemId = 1L;
        when(mockBookingService.getBookingWithUserBookedItem(itemId, userId)).thenReturn(null);
        String error = String.format("Пользователь с id %s не арендовал вещь с id %s", userId, itemId);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.createComment(comment, itemId, userId)
        );

        assertEquals(error, exception.getMessage());
        verify(mockCommentRepository, times(0)).save(any());
    }


    @Test
    void createComment_whenUserIsBooker_thenReturnedCommentDto() {
        long userId = 2L;
        long itemId = 1L;
        when(mockBookingService.getBookingWithUserBookedItem(itemId, userId)).thenReturn(booking);
        when(mockItemMapper.toCommentDto(any())).thenReturn(comment);

        CommentDto actualComment = itemService.createComment(comment, itemId, userId);

        assertThat(actualComment.getId(), equalTo(comment.getId()));
        assertThat(actualComment.getText(), equalTo(comment.getText()));
        assertThat(actualComment.getItem(), equalTo(comment.getItem()));
        verify(mockCommentRepository, times(1)).save(any());
    }


    @Test
    void findItemById_whenItemFound_thenReturnedItem() {
        long itemId = item.getId();
        when(mockItemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Item actualItem = itemService.findItemById(itemId);

        assertNotNull(actualItem);
        assertThat(actualItem.getId(), equalTo(item.getId()));
        assertThat(actualItem.getDescription(), equalTo(item.getDescription()));
    }

    @Test
    void findItemById_whenItemNotFound_thenNotFoundExceptionThrow() {
        long itemId = item.getId();
        String error = String.format("Вещь с ID=" + itemId + " не найдена!");
        when(mockItemRepository.findById(itemId)).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.findItemById(itemId)
        );

        assertEquals(error, exception.getMessage());
    }

    @Test
    void getItemsByRequestId() {
        long requestId = 1L;
        when(mockItemRepository.findAllByRequestId(requestId, Sort.by(Sort.Direction.DESC, "id"))).thenReturn(List.of(item));
        when(mockItemMapper.toItemAnswerRequestDto(any())).thenReturn(itemAnswerRequest);

        List<ItemAnswerRequestDto> actualItemAnswer = itemService.getItemsByRequestId(requestId);

        assertNotNull(actualItemAnswer);
        assertThat(actualItemAnswer.get(0).getId(), equalTo(item.getId()));
        assertThat(actualItemAnswer.get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(actualItemAnswer.get(0).getName(), equalTo(item.getName()));
        assertThat(actualItemAnswer.get(0).getAvailable(), equalTo(item.getAvailable()));
        assertThat(actualItemAnswer.get(0).getRequestId(), equalTo(item.getRequestId()));
    }

}