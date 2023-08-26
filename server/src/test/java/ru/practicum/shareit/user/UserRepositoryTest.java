package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository repository;

    private final User user = new User(
            null,
            "Павел",
            "p@mail.com");

    @Test
    public void contextLoads() {
        assertNotNull(em);
    }

    @Test
    void findByEmail_thenReturnUsers() {
        em.persist(user);

        List<User> actualList = repository.findByEmail(user.getEmail());

        assertNotNull(actualList);
        assertEquals(user.getName(), actualList.get(0).getName());

    }
}
