package ru.practicum.shareit.item;

import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemDtoWithBookings {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private Booking lastBooking;
    private Booking nextBooking;
    private Collection<CommentDtoToReturn> comments;

    @Data
    public static class Booking {

        protected Long id;
        protected Long bookerId;
    }
}