package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemAnswerRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class ItemRequestServiceIntegrationTest {

    private final EntityManager em;
    private final ItemRequestService itemRequestService;

    @Test
    void getItemRequestsByRequesterId() {
        User requester = new User(null, "User1", "u@email.com");
        em.persist(requester);

        LocalDateTime created = LocalDateTime.now();
        ItemRequest itemRequest = new ItemRequest(null, "description",  requester, created);
        em.persist(itemRequest);

        List<ItemRequestDto> actualItemRequests = itemRequestService.getItemRequestsByRequesterId(requester.getId());

        assertThat(actualItemRequests.get(0).getId(), notNullValue());
        assertThat(actualItemRequests.get(0).getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(actualItemRequests.get(0).getRequester(), equalTo(requester));
        assertThat(actualItemRequests.get(0).getCreated(), equalTo(created));
        assertThat(actualItemRequests.get(0).getItems(), equalTo(new ArrayList<>()));
    }

    @Test
    void getItemRequests() {
        User user = new User(null, "User", "user@email.com");
        em.persist(user);

        User requester = new User(null, "Requester", "req@email.com");
        em.persist(requester);

        LocalDateTime created = LocalDateTime.now();
        ItemRequest itemRequest = new ItemRequest(null, "description",  requester, created);
        em.persist(itemRequest);

        List<ItemRequestDto> actualItemRequests = itemRequestService.getItemRequests(user.getId(), 0, 10);

        assertThat(actualItemRequests.get(0).getId(), notNullValue());
        assertThat(actualItemRequests.get(0).getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(actualItemRequests.get(0).getRequester(), equalTo(requester));
        assertThat(actualItemRequests.get(0).getCreated(), equalTo(created));
        assertThat(actualItemRequests.get(0).getItems(), equalTo(new ArrayList<>()));
    }


    private ItemRequestDto makeItemRequestDto(String description, User requester, LocalDateTime created,
                                              List<ItemAnswerRequestDto> items) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription(description);
        dto.setRequester(requester);
        dto.setCreated(created);
        dto.setItems(items);

        return dto;
    }
}
