package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EmailExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private UserMapper mockUserMapper;

    @InjectMocks
    private UserServiceImpl userService;
    private UserDto expectedUser;
    private User user;
    private UserDto userToUpdate;

    @BeforeEach
    void beforeEach() {
        expectedUser = new UserDto();
        expectedUser.setId(1L);
        expectedUser.setName("Ivan");
        expectedUser.setEmail("ivan@email.ru");

        user = new User();
        user.setId(1L);
        user.setName("Ivan");
        user.setEmail("ivan@email.ru");

        userToUpdate = new UserDto();
        userToUpdate.setId(1L);
        userToUpdate.setName("IvanIvan");
        userToUpdate.setEmail("ivan2@email.ru");

    }

    @Test
    void createUser_whenUserEmailValid_thenSavedUser() {
        when(mockUserMapper.toUser(expectedUser)).thenReturn(user);
        when(mockUserMapper.toUserDto(user)).thenReturn(expectedUser);
        when(mockUserRepository.save(user)).thenReturn(user);

        UserDto actualUser = userService.createUser(expectedUser);

        assertThat(actualUser.getName(), equalTo(expectedUser.getName()));
        assertThat(actualUser.getEmail(), equalTo(expectedUser.getEmail()));
        verify(mockUserRepository, times(1)).save(any());
    }

    @Test
    void createUser_whenUserEmailIsEmpty_thenValidationExceptionThrow() {
        expectedUser.setEmail("");
        String error = "Адрес электронной почты не может быть пустым.";

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.createUser(expectedUser)
        );

        assertEquals(error, exception.getMessage());
        verify(mockUserRepository, times(0)).save(any());
    }


    @Test
    void createUser_whenUserEmailNotValid_thenValidationExceptionThrow() {
        expectedUser.setEmail("notvalidemail");
        String error = "Адрес электронной почты должен содержать символ @.";

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.createUser(expectedUser)
        );

        assertEquals(error, exception.getMessage());
        verify(mockUserRepository, times(0)).save(any());
    }


    @Test
    void getListAllUsers_whenListEmpty_thenReturnedEmptyList() {
        when(mockUserRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> users = userService.getListAllUsers();

        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    void getListAllUsers_whenListContainOneUser_thenReturnedUser() {
        when(mockUserMapper.toUserDto(user)).thenReturn(expectedUser);
        when(mockUserRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> actualUsers = userService.getListAllUsers();

        assertNotNull(actualUsers);
        assertEquals(1, actualUsers.size());
        assertThat(actualUsers.get(0).getName(), equalTo(user.getName()));
    }

    @Test
    void getUserById_whenUserFound_thenReturnedUser() {
        long userId = user.getId();
        when(mockUserMapper.toUserDto(user)).thenReturn(expectedUser);
        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto actualUser = userService.getUserById(userId);

        assertNotNull(actualUser);
        assertThat(actualUser.getName(), equalTo(expectedUser.getName()));
        assertThat(actualUser.getEmail(), equalTo(expectedUser.getEmail()));
    }

    @Test
    void getUserById_whenUserNotFound_thenReturnedNotFoundExceptionThrown() {
        long userIdNotFound = 0L;
        String error = String.format("Пользователь с id %d не найден", userIdNotFound);
        when(mockUserRepository.findById(userIdNotFound)).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(userIdNotFound));

        assertEquals(error, exception.getMessage());
    }

    @Test
    void updateUser_whenUserNotFound_thenReturnedNotFoundExceptionThrown() {
        long userIdNotFound = 0L;
        String error = String.format("Пользователь с id %d не найден", userIdNotFound);
        when(mockUserRepository.findById(userIdNotFound)).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.updateUser(userIdNotFound, userToUpdate));

        assertEquals(error, exception.getMessage());
    }

    @Test
    void updateUser_whenUserFoundAndEmailAlreadyExist_thenReturnedEmailExistExceptionThrown() {
        long userId = user.getId();
        String error = String.format("Пользователь с Email=" + userToUpdate.getEmail() + " уже существует!");
        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mockUserRepository.findByEmail(userToUpdate.getEmail())).thenReturn(List.of(new User(2L, "Dima", "ivan2@email.ru")));

        EmailExistException exception = assertThrows(
                EmailExistException.class,
                () -> userService.updateUser(userId, userToUpdate));

        assertEquals(error, exception.getMessage());
        verify(mockUserRepository, never()).save(any());
    }

    @Test
    void updateUser_whenUserFoundAndEmailNotExist_thenSaveUpdatedUser() {
        long userId = user.getId();
        when(mockUserMapper.toUserDto(user)).thenReturn(userToUpdate);
        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mockUserRepository.save(any())).thenReturn(new User(1L, "IvanIvan", "ivan2@email.ru"));

        UserDto actualUser = userService.updateUser(userId, userToUpdate);

        assertThat(actualUser.getName(), equalTo(userToUpdate.getName()));
        assertThat(actualUser.getEmail(), equalTo(userToUpdate.getEmail()));
    }

    @Test
    void updateUser_whenUserToUpdateIdIsNull_thenSaveUpdatedUser() {
        long userId = user.getId();
        userToUpdate.setId(null);
        when(mockUserMapper.toUserDto(user)).thenReturn(userToUpdate);
        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mockUserRepository.save(any())).thenReturn(new User(1L, "IvanIvan", "ivan2@email.ru"));

        UserDto actualUser = userService.updateUser(userId, userToUpdate);

        assertThat(actualUser.getName(), equalTo(userToUpdate.getName()));
        assertThat(actualUser.getEmail(), equalTo(userToUpdate.getEmail()));
    }

    @Test
    void deleteUser() {
        long userId = 1L;

        userService.deleteUserById(userId);

        verify(mockUserRepository, times(1)).deleteById(userId);
    }
}




