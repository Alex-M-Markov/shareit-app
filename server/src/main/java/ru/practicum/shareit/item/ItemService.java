package ru.practicum.shareit.item;

import java.util.Collection;

public interface ItemService {

    ItemDto create(Long userId, ItemDto item);

    ItemDto update(Long userId, Long itemId, ItemDto item);

    ItemDto getItemById(Long itemId);

    ItemDtoWithBookings getItemByIdWithBookings(Long userId, Long itemId);

    Collection<ItemDtoWithBookings> getAllItemsOfUser(Long userId, Integer firstElement,
        Integer numberOfElements);

    Collection<ItemDto> getAllMatchingItems(Long userId, String text, Integer firstElement,
        Integer numberOfElements);

    CommentDtoToReturn postComment(Long userId, Long itemId, CommentDto commentDto);

}