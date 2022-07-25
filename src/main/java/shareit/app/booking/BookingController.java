package shareit.app.booking;

import java.util.Collection;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
@AllArgsConstructor
public class BookingController {

    private final BookingServiceImpl bookingServiceImpl;
    private static final String USER_HEADER = "X-Sharer-User-Id";


    @PostMapping
    public BookingDtoToReturn create(@RequestHeader(USER_HEADER) Long userId,
        @Valid @RequestBody BookingDtoIncoming booking) {
        return bookingServiceImpl.create(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoToReturn update(@RequestHeader(USER_HEADER) Long userId,
        @PathVariable Long bookingId, @RequestParam Boolean approved) {
        return bookingServiceImpl.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoToReturn getBookingById(@RequestHeader(USER_HEADER) Long userId,
        @PathVariable Long bookingId) {
        return bookingServiceImpl.getBookingById(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDtoToReturn> getAllBookingsOfUser(
        @RequestHeader(USER_HEADER) Long userId,
        @RequestParam(name = "state", required = false, defaultValue = "ALL")
        BookingIncomingStates bookingState) {
        return bookingServiceImpl.getAllBookingsOfUser(userId, bookingState);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoToReturn> getAllBookingsOfUserItems(
        @RequestHeader(USER_HEADER) Long userId,
        @RequestParam(name = "state", required = false, defaultValue = "ALL")
        BookingIncomingStates bookingState) {
        return bookingServiceImpl.getAllBookingsOfUserItems(userId, bookingState);
    }

}