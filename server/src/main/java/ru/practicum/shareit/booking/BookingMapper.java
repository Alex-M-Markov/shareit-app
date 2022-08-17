package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.BookingDtoToReturn.Booker;
import ru.practicum.shareit.booking.BookingDtoToReturn.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;

public class BookingMapper {

    public static BookingDtoToReturn toBookingDto(Booking booking) {
        BookingDtoToReturn bookingDtoToReturn = new BookingDtoToReturn(booking.getId(),
            booking.getStart(), booking.getEnd(),
            new Item(), new Booker(), booking.getStatus());
        bookingDtoToReturn.getItem().setId(booking.getItem().getId());
        bookingDtoToReturn.getItem().setName(booking.getItem().getName());
        bookingDtoToReturn.getBooker().setId(booking.getBooker().getId());
        bookingDtoToReturn.getBooker().setName(booking.getBooker().getName());
        return bookingDtoToReturn;
    }

    public static Booking dtoIncomingToBooking(BookingDtoIncoming bookingDto, UserDto user,
        ItemDto item) {
        return new Booking(bookingDto.getId(), bookingDto.getStart(), bookingDto.getEnd(),
            ItemMapper.dtoToItem(item, user), UserMapper.dtoToUser(user), bookingDto.getStatus());
    }

}