package ru.practicum.shareit.item;

import java.util.Collection;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private final ItemServiceImpl itemServiceImpl;
    private static final String USER_HEADER = "X-Sharer-User-Id";


    @PostMapping
    public ItemDto create(@RequestHeader(USER_HEADER) Long userId,
        @RequestBody ItemDto item) {
        return itemServiceImpl.create(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_HEADER) Long userId,
        @PathVariable Long itemId, @RequestBody ItemDto item) {
        return itemServiceImpl.update(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookings getItemByIdWithBookings(@RequestHeader(USER_HEADER) Long userId,
        @PathVariable Long itemId) {
        return itemServiceImpl.getItemByIdWithBookings(userId, itemId);
    }

    @GetMapping
    public Collection<ItemDtoWithBookings> getAllItemsOfUser(
        @RequestHeader(USER_HEADER) Long userId,
        @RequestParam(name = "from", required = false) Integer firstElement,
        @RequestParam(name = "size", required = false) Integer numberOfElements) {
        return itemServiceImpl.getAllItemsOfUser(userId, firstElement, numberOfElements);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getAllMatchingItems(@RequestHeader(USER_HEADER) Long userId,
        @RequestParam String text,
        @RequestParam(name = "from", required = false) Integer firstElement,
        @RequestParam(name = "size", required = false) Integer numberOfElements) {
        return itemServiceImpl.getAllMatchingItems(userId, text, firstElement, numberOfElements);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoToReturn postComment(@RequestHeader(USER_HEADER) Long userId,
        @PathVariable Long itemId, @RequestBody CommentDto commentDto) {
        return itemServiceImpl.postComment(userId, itemId, commentDto);
    }

}