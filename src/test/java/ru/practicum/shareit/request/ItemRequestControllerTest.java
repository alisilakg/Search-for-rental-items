package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    LocalDateTime created = LocalDateTime.now();

    private final User user = new User(
            1L,
            "John",
            "john.doe@mail.com");

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(
            1L,
            "Кусторез",
            user,
            created,
            null);

    private long userId = user.getId();
    private long requestId = itemRequestDto.getId();

    @Test
    void createNewItemRequestWithException() throws Exception {
        itemRequestDto.setDescription("");
        String error = "Описание запроса вещи не может быть пустым.";
        when(itemRequestService.createItemRequest(any(), anyLong(), any(LocalDateTime.class)))
                .thenThrow(new ValidationException(error));

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400));
    }

    @Test
    void createNewItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(any(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requester.id", is(itemRequestDto.getRequester().getId()), Long.class))
                .andExpect(jsonPath("$.requester.name", is(itemRequestDto.getRequester().getName())))
                .andExpect(jsonPath("$.requester.email", is(itemRequestDto.getRequester().getEmail())))
                .andExpect(jsonPath("$.created",
                        is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));

        verify(itemRequestService, times(1)).createItemRequest(any(), anyLong(), any(LocalDateTime.class));
    }

    @Test
    void getItemRequestsByRequester() throws Exception {
        when(itemRequestService.getItemRequestsByRequesterId(userId))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].requester.id", is(itemRequestDto.getRequester().getId()), Long.class))
                .andExpect(jsonPath("$[0].requester.name", is(itemRequestDto.getRequester().getName())))
                .andExpect(jsonPath("$[0].requester.email", is(itemRequestDto.getRequester().getEmail())))
                .andExpect(jsonPath("$[0].created",
                        is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));

        verify(itemRequestService, times(1)).getItemRequestsByRequesterId(anyLong());
    }

    @Test
    void getItemRequestsByRequester_thenReturnedEmptyList() throws Exception {
        when(itemRequestService.getItemRequestsByRequesterId(userId))
                .thenReturn(new ArrayList<>());

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getItemRequestById_whenUserNotFound_thenNotFoundExceptionThrown() throws Exception {
        String error = String.format("Запроса с ID=" + requestId + " не найдено!");
        when(itemRequestService.getItemRequestById(requestId, userId))
                .thenThrow(new NotFoundException(error));

        mvc.perform(get("/requests/{id}", requestId)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(itemRequestService).getItemRequestById(anyLong(), anyLong());
    }

    @Test
    void getItemRequestById_whenUserFound_thenReturnedUser() throws Exception {
        when(itemRequestService. getItemRequestById(requestId, userId))
                .thenReturn(itemRequestDto);

        mvc.perform(get("/requests/{id}", requestId)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requester.id", is(itemRequestDto.getRequester().getId()), Long.class))
                .andExpect(jsonPath("$.requester.name", is(itemRequestDto.getRequester().getName())))
                .andExpect(jsonPath("$.requester.email", is(itemRequestDto.getRequester().getEmail())))
                .andExpect(jsonPath("$.created",
                        is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));

        verify(itemRequestService, times(1)). getItemRequestById(anyLong(), anyLong());
    }

    @Test
    void getItemRequests() throws Exception {
        when(itemRequestService.getItemRequests(userId, 0, 10))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].requester.id", is(itemRequestDto.getRequester().getId()), Long.class))
                .andExpect(jsonPath("$[0].requester.name", is(itemRequestDto.getRequester().getName())))
                .andExpect(jsonPath("$[0].requester.email", is(itemRequestDto.getRequester().getEmail())))
                .andExpect(jsonPath("$[0].created",
                        is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void getItemRequests_thenReturnedEmptyList() throws Exception {
        when(itemRequestService.getItemRequests(userId, 0, 10))
                .thenReturn(new ArrayList<>());

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

}