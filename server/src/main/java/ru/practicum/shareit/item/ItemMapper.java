package ru.practicum.shareit.item;

import java.util.Collection;
import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;

@Data
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
            item.getRequestId()
        );
    }

    public static ItemDtoWithBookings toItemDtoWithBookings(Item item, Booking lastBooking,
        Booking nextBooking, Collection<CommentDtoToReturn> comments) {
        ItemDtoWithBookings itemDtoWithBookings = new ItemDtoWithBookings(item.getId(),
            item.getName(), item.getDescription(), item.getAvailable(), item.getRequestId(),
            new ItemDtoWithBookings.Booking(), new ItemDtoWithBookings.Booking(), comments);
        if (lastBooking == null && nextBooking == null) {
            itemDtoWithBookings.setNextBooking(null);
            itemDtoWithBookings.setLastBooking(null);
            return itemDtoWithBookings;
        } else if (lastBooking == null) {
            itemDtoWithBookings.setLastBooking(null);
            itemDtoWithBookings.getNextBooking().setId(nextBooking.getId());
            itemDtoWithBookings.getNextBooking().setBookerId(nextBooking.getBooker().getId());
            return itemDtoWithBookings;
        } else if (nextBooking == null) {
            itemDtoWithBookings.setNextBooking(null);
            itemDtoWithBookings.getLastBooking().setId(lastBooking.getId());
            itemDtoWithBookings.getLastBooking().setBookerId(lastBooking.getBooker().getId());
            return itemDtoWithBookings;
        } else {
            itemDtoWithBookings.getLastBooking().setId(lastBooking.getId());
            itemDtoWithBookings.getLastBooking().setBookerId(lastBooking.getBooker().getId());
            itemDtoWithBookings.getNextBooking().setId(nextBooking.getId());
            itemDtoWithBookings.getNextBooking().setBookerId(nextBooking.getBooker().getId());
            return itemDtoWithBookings;
        }
    }

    public static Item dtoToItem(ItemDto itemDto, UserDto user) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(),
            itemDto.getAvailable(), UserMapper.dtoToUser(user), itemDto.getRequestId());
    }

}