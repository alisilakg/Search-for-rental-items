package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import static ru.practicum.shareit.validation.ValidationGroups.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    @NotBlank(groups = Create.class, message = "Имя не может быть пустым")
    private String name;

    @NotBlank(groups = Create.class, message = "E-mail не может быть пустым")
    @Email
    private String email;
}