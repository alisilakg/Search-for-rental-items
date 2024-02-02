package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import static ru.practicum.shareit.validation.ValidationGroups.Create;
import static ru.practicum.shareit.validation.ValidationGroups.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    @NotBlank(groups = Create.class, message = "Название не может быть пустым")
    @Size(groups = {Create.class, Update.class}, max = 256, message = "Название должно быть до 256 символов")
    private String name;

    @NotBlank(groups = Create.class, message = "Описание не может быть пустым")
    @Size(groups = {Create.class, Update.class}, max = 1024, message = "Длина описания должна быть до 1024 символов")
    private String description;

    @NotNull(groups = Create.class, message = "Поле доступности вещи не может быть пустым")
    private Boolean available;

    private Long requestId;
}