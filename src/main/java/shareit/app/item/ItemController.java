package shareit.app.item;

import java.util.Collection;
import javax.validation.Valid;
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
        @Valid @RequestBody ItemDto item) {
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
    public Collection<ItemDtoWithBookings> getAllItemsOfUser(@RequestHeader(USER_HEADER) Long userId) {
        return itemServiceImpl.getAllItemsOfUser(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getAllMatchingItems(@RequestParam String text) {
        return itemServiceImpl.getAllMatchingItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoToReturn postComment(@RequestHeader(USER_HEADER) Long userId,
        @PathVariable Long itemId, @Valid @RequestBody CommentDto text) {
        return itemServiceImpl.postComment(userId, itemId, text);
    }

}