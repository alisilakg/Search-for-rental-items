package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    @Email
    @NotEmpty
    @NotBlank
    private String email;
}
