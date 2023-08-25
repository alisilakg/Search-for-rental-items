package ru.practicum.shareit.item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemAnswerRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ItemMapperTest {

    @Autowired
    private ItemMapper itemMapper;

    Item item;
    LocalDateTime now;
    User owner;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
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
    }

    @Test
    void toItemAnswerRequestDto() {
        ItemAnswerRequestDto expected = new ItemAnswerRequestDto();
        expected.setId(1L);
        expected.setName("name");
        expected.setDescription("description");
        expected.setAvailable(true);
        expected.setRequestId(1L);

        ItemAnswerRequestDto actual = itemMapper.toItemAnswerRequestDto(item);

        assertEquals(expected, actual);
    }
}
