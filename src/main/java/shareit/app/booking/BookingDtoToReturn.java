package shareit.app.booking;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingDtoToReturn {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private Booker booker;
    private BookingStatus status;

    @Data
    protected static class Item {

        protected Long id;
        protected String name;
    }

    @Data
    protected static class Booker {

        private Long id;
        private String name;
    }

}