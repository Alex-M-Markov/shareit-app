package shareit.app.booking;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BookingStatus {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED;
}