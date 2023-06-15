package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    @Email
    @NotBlank
    @NotEmpty
    private String email;

//    public UserDto(ong id, String name, String email) {
//        this.id = id;
//        this.name = name;
//        this.email = email;
//    }
}
