package ru.practicum.shareit.request;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class ItemRequestDtoWithAnswers {

    private Long id;
    private String description;
    private User requestor;
    private List<ItemAnswer> items;
    private LocalDateTime created;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {

        protected Long id;
        protected String name;
        protected String email;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemAnswer {

        protected Long id;
        protected String name;
        protected String description;
        protected Boolean available;
        protected Long requestId;
    }


}