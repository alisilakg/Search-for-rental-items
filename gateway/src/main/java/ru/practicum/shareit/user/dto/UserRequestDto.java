package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static ru.practicum.shareit.validation.ValidationGroups.Create;
import static ru.practicum.shareit.validation.ValidationGroups.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    @NotBlank(groups = Create.class, message = "Имя не может быть пустым")
    @Size(groups = {Create.class, Update.class}, max = 256, message = "Имя должно быть до 256 символов")
    private String name;

    @NotBlank(groups = Create.class, message = "E-mail не может быть пустым")
    @Size(groups = {Create.class, Update.class}, max = 512, message = "Email должен быть до 512 символов")
    @Email(groups = {Create.class, Update.class})
    private String email;
}