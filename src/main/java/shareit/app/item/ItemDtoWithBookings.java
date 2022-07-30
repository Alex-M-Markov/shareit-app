package shareit.app.item;

import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Data;
import shareit.app.request.ItemRequest;

@Data
@AllArgsConstructor
public class ItemDtoWithBookings {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest request;
    private Booking lastBooking;
    private Booking nextBooking;
    private Collection<CommentDtoToReturn> comments;

    @Data
    protected static class Booking {

        protected Long id;
        protected Long bookerId;
    }
}