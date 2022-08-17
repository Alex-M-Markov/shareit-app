package ru.practicum.shareit.booking;

import java.util.Collection;

public interface BookingService {

    BookingDtoToReturn create(Long userId, BookingDtoIncoming booking);

    BookingDtoToReturn update(Long userId, Long bookingId, Boolean approved);

    BookingDtoToReturn getBookingById(Long userId, Long bookingId);

    Collection<BookingDtoToReturn> getAllBookingsOfUser(Long userId,
        BookingIncomingStates bookingState, Integer firstElement, Integer numberOfElements);

    Collection<BookingDtoToReturn> getAllBookingsOfUserItems(Long userId,
        BookingIncomingStates bookingState, Integer firstElement, Integer numberOfElements);
}