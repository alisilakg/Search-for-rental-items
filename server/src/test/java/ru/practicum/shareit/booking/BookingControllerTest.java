package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    BookingService service;

    private final BookingInputDto bookingInputDto = new BookingInputDto(
            1L,
            LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusHours(20));

    private final UserDto userDto = new UserDto(
            1L,
            "User",
            "v@mail.com");

    private final ItemDto itemDto = new ItemDto(
            1L,
            "Shtill",
            "chainsaw",
            true,
            null,
            null,
            null,
            null);

    private final BookingDto bookingDto = new BookingDto(
            1L,
            null,
            null,
            itemDto,
            userDto,
            Status.APPROVED);

    private final int from = 0;
    private final int size = 30;
    private final String state = "ALL";

//    @Test
//    void create_thenReturnBooking() throws Exception {
//        when(service.create(bookingInputDto, userDto.getId())).thenReturn(bookingDto);
//
//        mvc.perform(post("/bookings")
//                        .content(mapper.writeValueAsString(bookingInputDto))
//                        .header("X-Sharer-User-Id", 1)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
//                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
//                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), String.class));
//    }

//    @Test
//    void create_whenEndIsBeforeStart_thenValidationExceptionThrown() throws Exception {
//        bookingInputDto.setEnd(LocalDateTime.now().plusMinutes(30));
//
//        mvc.perform(post("/bookings")
//                        .content(mapper.writeValueAsString(bookingInputDto))
//                        .header("X-Sharer-User-Id", 1)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().is(400));
//    }

//    @Test
//    void create_whenEndIsEqualsStart_thenValidationExceptionThrown() throws Exception {
//        bookingInputDto.setEnd(bookingInputDto.getStart());
//
//        mvc.perform(post("/bookings")
//                        .content(mapper.writeValueAsString(bookingInputDto))
//                        .header("X-Sharer-User-Id", 1)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().is(400));
//    }

    @Test
    void update_thenReturnBooking() throws Exception {
        boolean approved = true;
        when(service.update(1L, 1L, approved)).thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), String.class));
    }

    @Test
    void getBookingById_thenReturnBooking() throws Exception {
        when(service.getBookingById(1L, 1L)).thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), String.class));
    }

    @Test
    void getBookings_thenReturnBookingList() throws Exception {
        when(service.getBookings(state, 1L, from, size))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class));
    }

    @Test
    void getBookingsOwner_thenReturnBookingList() throws Exception {
        when(service.getBookingsOwner(state, 1L, from, size))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class));
    }
}
