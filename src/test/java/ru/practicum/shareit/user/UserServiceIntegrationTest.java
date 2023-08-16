package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class UserServiceIntegrationTest {
    private final EntityManager em;
    private final UserService service;

    @Test
    void createUser() {
        UserDto userDto = makeUserDto("Ivan", "ivan@email.ru");
        service.createUser(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User actualUser = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(actualUser.getId(), notNullValue());
        assertThat(actualUser.getName(), equalTo(userDto.getName()));
        assertThat(actualUser.getEmail(), equalTo(userDto.getEmail()));
    }


    @Test
    void getListAllUsers() {
        UserDto userDto1 = makeUserDto("Ivan", "ivan@email.ru");
        service.createUser(userDto1);

        UserDto userDto2 = makeUserDto("Dima", "dima@email.ru");
        service.createUser(userDto2);

        List<UserDto> actualUsers = service.getListAllUsers();

        assertThat(actualUsers, hasSize(2));
        assertThat(actualUsers.get(0).getId(), notNullValue());
        assertThat(actualUsers.get(0).getName(), equalTo(userDto1.getName()));
        assertThat(actualUsers.get(0).getEmail(), equalTo(userDto1.getEmail()));
        assertThat(actualUsers.get(1).getId(), notNullValue());
        assertThat(actualUsers.get(1).getName(), equalTo(userDto2.getName()));
        assertThat(actualUsers.get(1).getEmail(), equalTo(userDto2.getEmail()));
    }


    @Test
    void getUserById() {
        UserDto userDto = makeUserDto("Ivan", "ivan@email.ru");
        service.createUser(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        UserDto actualUser = service.getUserById(user.getId());

        assertThat(actualUser.getId(), notNullValue());
        assertThat(actualUser.getName(), equalTo(userDto.getName()));
        assertThat(actualUser.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void updateUser() {
        UserDto userDto = makeUserDto("Ivan", "ivan@email.ru");
        service.createUser(userDto);

        UserDto userToUpdate = makeUserDto("IvanIvan", "ivan2@email.ru");

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        UserDto actualUser = service.updateUser(user.getId(), userToUpdate);

        assertThat(actualUser.getId(), notNullValue());
        assertThat(actualUser.getName(), equalTo(userToUpdate.getName()));
        assertThat(actualUser.getEmail(), equalTo(userToUpdate.getEmail()));
    }

    @Test
    void deleteUserById() {
        UserDto userDto = makeUserDto("Ivan", "ivan@email.ru");
        service.createUser(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        service.deleteUserById(user.getId());

        String error = String.format("Пользователь с id %d не найден", user.getId());
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.getUserById(user.getId())
        );

        assertEquals(error, exception.getMessage());
    }


    private UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);

        return dto;
    }

}
