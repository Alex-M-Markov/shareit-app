package ru.practicum.shareit.item;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.User;

@Data
@AllArgsConstructor
public class CommentDto {

    private Long id;
    private String text;
    private Item item;
    private User author;
    private LocalDateTime created;

}