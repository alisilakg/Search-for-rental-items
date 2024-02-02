package ru.practicum.shareit.item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private final User user = new User(
            null,
            "User",
            "a@mail.ru");

    private final Item item = new Item(
            null,
            "Кусторез",
            "Бывалый",
            true,
            user,
            null);

    private final Comment comment = new Comment(
            null,
            "Отличный кусторез",
            item,
            user,
            LocalDateTime.now());

    @Test
    void findAllByItem_Id() {
        userRepository.save(user);
        itemRepository.save(item);
        commentRepository.save(comment);

        List<Comment> actualList = commentRepository.findByItemId(item.getId(),
                Sort.by(Sort.Direction.DESC, "created"));

        assertNotNull(actualList);
        assertEquals(comment.getText(), actualList.get(0).getText());
    }
}
