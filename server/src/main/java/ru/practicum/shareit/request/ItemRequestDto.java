package ru.practicum.shareit.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {

    private Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {

        protected Long id;
        protected String name;
        protected String email;
    }
}