package shareit.app.booking;

import java.util.Collection;

public interface BookingService {

    BookingDtoToReturn create(Long userId, BookingDtoIncoming booking);

    BookingDtoToReturn update(Long userId, Long bookingId, Boolean approved);

    BookingDtoToReturn getBookingById(Long userId, Long bookingId);

    Collection<BookingDtoToReturn> getAllBookingsOfUser(Long userId,
        BookingIncomingStates bookingState);

    Collection<BookingDtoToReturn> getAllBookingsOfUserItems(Long userId,
        BookingIncomingStates bookingState);
}