package shareit.app.booking;

import shareit.app.booking.BookingDtoToReturn.Booker;
import shareit.app.booking.BookingDtoToReturn.Item;
import shareit.app.item.ItemDto;
import shareit.app.item.ItemMapper;
import shareit.app.user.UserDto;
import shareit.app.user.UserMapper;

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