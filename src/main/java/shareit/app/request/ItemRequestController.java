package shareit.app.request;

import java.util.Collection;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/requests")
@AllArgsConstructor
public class ItemRequestController {

    private final ItemRequestServiceImpl itemRequestServiceImpl;
    private static final String USER_HEADER = "X-Sharer-User-Id";


    @PostMapping
    public ItemRequestDto create(@RequestHeader(USER_HEADER) Long userId,
        @Valid @RequestBody ItemRequestDto itemRequest) {
        return itemRequestServiceImpl.create(userId, itemRequest);
    }

    @GetMapping
    public Collection<ItemRequestDtoWithAnswers> getAllRequestsOfUser(
        @RequestHeader(USER_HEADER) Long userId) {
        return itemRequestServiceImpl.getAllRequestsOfUser(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDtoWithAnswers> getAllRequestsOfOtherUsers(
        @RequestHeader(USER_HEADER) Long userId,
        @RequestParam(name = "from", required = false) Integer firstElement,
        @RequestParam(name = "size", required = false) Integer numberOfElements) {
        return itemRequestServiceImpl.getAllRequestsOfOtherUsers(userId, firstElement,
            numberOfElements);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithAnswers getRequestById(@RequestHeader(USER_HEADER) Long userId,
        @PathVariable Long requestId) {
        return itemRequestServiceImpl.getRequestById(userId, requestId);
    }

}