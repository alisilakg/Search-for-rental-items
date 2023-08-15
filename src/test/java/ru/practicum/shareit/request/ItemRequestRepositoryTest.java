package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRequestRepository repository;

    private final User requester = new User(
            null,
            "Павел",
            "p@mail.com");

    private final ItemRequest request = new ItemRequest(
            null,
            "Кусторез",
            requester,
            LocalDateTime.now());

    private static final Sort SORT = Sort.by(Sort.Direction.DESC, "created");

    @Test
    public void contextLoads() {
        assertNotNull(em);
    }

    @Test
    void findByRequesterId_thenReturnRequest() {
        em.persist(requester);
        em.persist(request);

        List<ItemRequest> actualList = repository.findByRequesterId(requester.getId());

        assertNotNull(actualList);
        assertEquals(request.getDescription(), actualList.get(0).getDescription());
    }

    @Test
    void findByRequesterIdNot_thenReturnRequest() {
        int size = 30;
        PageRequest page = PageRequest.of(0, size, SORT);
        em.persist(requester);
        em.persist(request);

        List<ItemRequest> actualList = repository.findByRequesterIdNot(2L, page);
        List<ItemRequest> emptyList = repository.findByRequesterIdNot(requester.getId(), page);

        assertEquals(1, actualList.size());
        assertEquals(0, emptyList.size());
    }
}
