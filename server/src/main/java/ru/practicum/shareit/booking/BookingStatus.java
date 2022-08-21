package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BookingStatus {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED
}