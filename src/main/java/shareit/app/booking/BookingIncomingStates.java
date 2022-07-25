package shareit.app.booking;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BookingIncomingStates {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED
}