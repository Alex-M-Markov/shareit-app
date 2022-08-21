package ru.practicum.shareit.request;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(USER_HEADER) long userId,
        @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Creating item request {}, userId={}", itemRequestDto, userId);
        return itemRequestClient.createRequest(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(USER_HEADER) long userId,
        @PathVariable long requestId) {
        log.info("Get item request {}, userId={}", requestId, userId);
        return itemRequestClient.getRequestById(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsOfUser(
        @RequestHeader(USER_HEADER) long userId) {
        log.info("Getting all item requests of user {}", userId);
        return itemRequestClient.getAllRequestsOfUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllBookingsOfUserItems(
        @RequestHeader(USER_HEADER) long userId,
        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all bookings of user {} items, from={}, size={}", userId, from,
            size);
        return itemRequestClient.getAllBookingsOfUserItems(userId, from, size);
    }

}