//package ru.practicum.shareit.item;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.data.domain.PageRequest;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.request.model.ItemRequest;
//import ru.practicum.shareit.user.model.User;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@DataJpaTest
//class ItemRepositoryTest {
//    @Autowired
//    private TestEntityManager em;
//
//    @Autowired
//    private ItemRepository repository;
//
//    private final User owner = new User(
//            null,
//            "Павел",
//            "p@mail.com");
//
//    private final User requester = new User(
//            null,
//            "User",
//            "a@mail.ru");
//
//    private final ItemRequest request = new ItemRequest(
//            null,
//            "description",
//            requester,
//            LocalDateTime.now());
//
//    private final Item item = new Item(
//            null,
//            "Кусторез",
//            "Бывалый",
//            true,
//            owner,
//            1L);
//
//    private final int size = 30;
//
//    private final PageRequest page = PageRequest.of(0, size);
//
//    @BeforeEach
//    void setUp() {
//        em.persist(requester);
//        em.persist(owner);
//        em.persist(request);
//        em.persist(item);
//    }
//
//    @Test
//    public void contextLoads() {
//        assertNotNull(em);
//    }
//
//    @Test
//    void findByOwnerId_thenReturnItem() {
//        List<Item> actualList = repository.findByOwnerId(owner.getId(), page);
//
//        assertNotNull(actualList);
//        assertEquals(item.getDescription(), actualList.get(0).getDescription());
//    }
//
//    @Test
//    void getItemsBySearchQuery_thenReturnItemsList() {
//        String text = "куст";
//
//        List<Item> actualList = repository.getItemsBySearchQuery(text, page);
//
//        assertNotNull(actualList);
//        assertEquals(item.getDescription(), actualList.get(0).getDescription());
//    }
//
//}
