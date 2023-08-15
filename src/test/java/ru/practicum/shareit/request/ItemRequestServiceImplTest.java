package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.CheckService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository mockItemRequestRepository;

    @Mock
    private CheckService mockCheckService;

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    @Mock
    private ItemRequestMapper mockItemRequestMapper;

    private User requester;
    private User owner;
    private Item item;
    private ItemRequest request;
    private ItemRequestDto itemRequestToSave;

    @BeforeEach
    void setup() {
        owner = new User();
        owner.setName("name");
        owner.setEmail("e@mail.ru");
        owner.setId(1L);

        requester = new User();
        requester.setName("name2");
        requester.setEmail("e2@mail.ru");
        requester.setId(2L);

        request = new ItemRequest();
        request.setDescription("description");
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());
        request.setId(1L);

        item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequestId(1L);

        itemRequestToSave = new ItemRequestDto(1L, "description", requester,
                LocalDateTime.now(), null);
    }


    @Test
    void createItemRequest_whenItemRequestValid_thenSaveItemRequest() {
        when(mockItemRequestRepository.save(any())).thenReturn(request);
        when(mockCheckService.isExistUser(owner.getId())).thenReturn(true);
        when(mockItemRequestMapper.toItemRequest(itemRequestToSave, owner.getId(),itemRequestToSave.getCreated())).thenReturn(request);
        when(mockItemRequestMapper.toItemRequestDto(request)).thenReturn(itemRequestToSave);

        ItemRequestDto actualRequestDto = itemRequestService.createItemRequest(itemRequestToSave,
                owner.getId(), itemRequestToSave.getCreated());

        assertNotNull(actualRequestDto);
        assertThat(actualRequestDto.getId(), equalTo(request.getId()));
        assertThat(actualRequestDto.getDescription(), equalTo(request.getDescription()));
        assertThat(actualRequestDto.getRequester(), equalTo(request.getRequester()));
        assertThat(actualRequestDto.getItems(), equalTo(itemRequestToSave.getItems()));
        verify(mockItemRequestRepository, times(1)).save(any());
    }

    @Test
    void createItemRequest_whenRequesterNotFound_thenReturnedNotFoundExceptionThrown() {
        long userNotFoundId = 0L;
        String error = String.format("Пользователь с ID=" + userNotFoundId + " не найден!");
        when(mockCheckService.isExistUser(userNotFoundId)).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.createItemRequest(itemRequestToSave, userNotFoundId,
                        itemRequestToSave.getCreated())
        );

        assertEquals(error, exception.getMessage());
        verify(mockItemRequestRepository, times(0)).save(any());
    }

    @Test
    void createItemRequest_whenDescriptionIsEmpty_thenReturnedValidationExceptionThrown() {
        String error = "Описание запроса вещи не может быть пустым.";
        itemRequestToSave.setDescription("");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemRequestService.createItemRequest(itemRequestToSave, requester.getId(),
                        itemRequestToSave.getCreated())
        );

        assertEquals(error, exception.getMessage());
        verify(mockItemRequestRepository, times(0)).save(any());
    }

    @Test
    void getItemRequestsByRequesterId_whenRequesterNotFound_thenNotFoundExceptionThrow() {
        long requesterNotFoundId = 0L;
        String error = String.format("Пользователь с ID=" + requesterNotFoundId + " не найден!");
        when(mockCheckService.isExistUser(requesterNotFoundId)).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getItemRequestsByRequesterId(requesterNotFoundId)
        );

        assertEquals(error, exception.getMessage());
        verify(mockItemRequestRepository, times(0)).save(any());
    }

    @Test
    void getItemRequestsByRequesterId_whenRequesterFound_thenReturnedListItemRequests() {
        long userId = requester.getId();
        when(mockCheckService.isExistUser(userId)).thenReturn(true);
        when(mockItemRequestMapper.toItemRequestDto(request)).thenReturn(itemRequestToSave);
        when(mockItemRequestRepository.findByRequesterId(userId)).thenReturn(List.of(request));

        List<ItemRequestDto> actualRequestDtos = itemRequestService.getItemRequestsByRequesterId(userId);

        assertNotNull(actualRequestDtos);
        assertThat(actualRequestDtos.get(0).getId(), equalTo(request.getId()));
        assertThat(actualRequestDtos.get(0).getDescription(), equalTo(request.getDescription()));
        assertThat(actualRequestDtos.get(0).getRequester(), equalTo(request.getRequester()));
        assertThat(actualRequestDtos.get(0).getItems(), equalTo(itemRequestToSave.getItems()));
    }

    @Test
    void getItemRequests_whenRequesterNotFound_thenNotFoundExceptionThrow() {
        long requesterNotFoundId = 0L;
        int from = 0;
        int size = 1;
        String error = String.format("Пользователь с ID=" + requesterNotFoundId + " не найден!");
        when(mockCheckService.isExistUser(requesterNotFoundId)).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getItemRequests(requesterNotFoundId, from, size)
        );

        assertEquals(error, exception.getMessage());
    }

    @Test
    void getItemRequests_whenRequesterFound_thenReturnedListOfItemRequests() {
        long userId = requester.getId();
        int from = 0;
        int size = 1;
        PageRequest page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        when(mockCheckService.isExistUser(userId)).thenReturn(true);
        when(mockItemRequestMapper.toItemRequestDto(request)).thenReturn(itemRequestToSave);
        when(mockItemRequestRepository.findByRequesterIdNot(userId, page)).thenReturn(List.of(request));

        List<ItemRequestDto> actualRequestDtos = itemRequestService.getItemRequests(userId, from, size);

        assertNotNull(actualRequestDtos);
        assertThat(actualRequestDtos.get(0).getId(), equalTo(request.getId()));
        assertThat(actualRequestDtos.get(0).getDescription(), equalTo(request.getDescription()));
        assertThat(actualRequestDtos.get(0).getRequester(), equalTo(request.getRequester()));
        assertThat(actualRequestDtos.get(0).getItems(), equalTo(itemRequestToSave.getItems()));
    }


    @Test
    void getItemRequestById_whenRequesterNotFound_thenNotFoundExceptionThrown() {
        long requesterNotFoundId = 0L;
        long requestId = request.getId();
        String error = String.format("Пользователь с ID=" + requesterNotFoundId + " не найден!");
        when(mockCheckService.isExistUser(requesterNotFoundId)).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getItemRequestById(requestId, requesterNotFoundId)
        );

        assertEquals(error, exception.getMessage());
    }
    @Test
    void getItemRequestById_whenRequestFound_thenReturnedItemRequest() {
        long userId = 0L;
        long requestId = request.getId();
        when(mockCheckService.isExistUser(userId)).thenReturn(true);
        when(mockItemRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(mockItemRequestMapper.toItemRequestDto(request)).thenReturn(itemRequestToSave);

        ItemRequestDto actualRequestDto = itemRequestService.getItemRequestById(requestId, userId);

        assertNotNull(actualRequestDto);
        assertThat(actualRequestDto.getId(), equalTo(request.getId()));
        assertThat(actualRequestDto.getDescription(), equalTo(request.getDescription()));
        assertThat(actualRequestDto.getRequester(), equalTo(request.getRequester()));
        assertThat(actualRequestDto.getItems(), equalTo(itemRequestToSave.getItems()));
    }

    @Test
    void getItemRequestById_whenRequestNotFound_thenNotFoundExceptionThrown() {
        long requesterNotFoundId = requester.getId();
        long requestId = request.getId();
        String error = String.format("Запроса с ID=" + requestId + " не найдено!");
        when(mockCheckService.isExistUser(requesterNotFoundId)).thenReturn(true);
        when(mockItemRequestRepository.findById(requestId)).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getItemRequestById(requestId, requesterNotFoundId)
        );

        assertEquals(error, exception.getMessage());
    }
}