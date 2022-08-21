package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentDto {

    private Long id;
    @NotNull
    @NotBlank
    private String text;
    private LocalDateTime created;

}