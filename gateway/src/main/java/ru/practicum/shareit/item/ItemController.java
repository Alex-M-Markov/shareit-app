package ru.practicum.shareit.item;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_HEADER) long userId,
        @RequestBody @Valid ItemDto itemDto) {
        log.info("Creating item {}, userId={}", itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(USER_HEADER) long userId,
        @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        log.info("Updating item {}, userId={}", itemId, userId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(USER_HEADER) long userId,
        @PathVariable Long itemId) {
        log.info("Get item {}, userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(USER_HEADER) long userId,
        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get items of userId={}, from={}, size={}", userId, from,
            size);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getAllMatchingItems(@RequestHeader(USER_HEADER) long userId,
        @RequestParam(name = "text", defaultValue = "") String text,
        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all matching items with text {} from={}, size={}", text, from,
            size);
        return itemClient.getAllMatchingItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestHeader(USER_HEADER) long userId,
        @PathVariable long itemId, @RequestBody CommentDto commentDto) {
        log.info("Publishing comment for item {} by user {}", itemId, userId);
        return itemClient.postComment(userId, itemId, commentDto);
    }

}