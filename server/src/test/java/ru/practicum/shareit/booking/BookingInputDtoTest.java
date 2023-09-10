package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingInputDtoTest {
    @Autowired
    private JacksonTester<BookingInputDto> json;

    @Test
    void testBookingInputDto() throws Exception {
        BookingInputDto bookingInputDto = new BookingInputDto(null, LocalDateTime.now().plusMinutes(20), LocalDateTime.now().plusHours(1L));

        JsonContent<BookingInputDto> result = json.write(bookingInputDto);

        assertThat(result).extractingJsonPathValue("$.start").isNotNull();
        assertThat(result).extractingJsonPathValue("$.end").isNotNull();

    }
}
