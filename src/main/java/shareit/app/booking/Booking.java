package shareit.app.booking;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import shareit.app.item.Item;
import shareit.app.user.User;

@Data
@AllArgsConstructor
public class Booking {

    private Long id;
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private User booker;
    private BookingStatus status;

}