package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BookingIncomingStates {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED,
    UNSUPPORTED_STATUS
}