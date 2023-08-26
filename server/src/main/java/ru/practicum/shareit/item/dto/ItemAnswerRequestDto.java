package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemAnswerRequestDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;

}
