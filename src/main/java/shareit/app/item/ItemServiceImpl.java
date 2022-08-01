package shareit.app.item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import shareit.app.booking.Booking;
import shareit.app.booking.BookingRepository;
import shareit.app.booking.BookingStatus;
import shareit.app.exceptions.CommentNotAllowedException;
import shareit.app.exceptions.ItemNotFoundException;
import shareit.app.exceptions.UserNotFoundException;
import shareit.app.user.UserMapper;
import shareit.app.user.UserRepository;
import shareit.app.user.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto create(Long userId, ItemDto item) {
        log.info("Создается вещь {} пользователя {}", item.getName(), userId);
        checkUserExistence(userId);
        ItemDto itemDto = ItemMapper.toItemDto(
            itemRepository.save((ItemMapper.dtoToItem(item, userService.getUserById(userId)))));
        log.info("Вещь {} успешно создан", itemDto.getName());
        return itemDto;
    }

    private void checkUserExistence(Long userId) {
        try {
            userService.getUserById(userId);
        } catch (EntityNotFoundException e) {
            throw new UserNotFoundException(e);
        }
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto item) {
        log.info("Обновляется вещь {} пользователя {}", item.getName(), userId);
        checkUserExistence(userId);
        checkUpdateRights(userId, itemId);
        ItemDto itemToUpdate = updateItemFields(itemId, item);
        ItemDto itemToReturn = ItemMapper.toItemDto(itemRepository.save((
            ItemMapper.dtoToItem(itemToUpdate, userService.getUserById(userId)))));
        log.info("Вещь {} успешно обновлена", itemToReturn.getName());
        return itemToReturn;
    }

    private ItemDto updateItemFields(Long id, ItemDto item) {
        ItemDto itemToUpdate = getItemById(id);
        if (itemToUpdate == null) {
            throw new ItemNotFoundException();
        }
        if (item.getName() != null) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }
        return itemToUpdate;
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        try {
            log.info("Получаем вещь #{}", itemId);
            return ItemMapper.toItemDto(itemRepository.getReferenceById((itemId)));
        } catch (EntityNotFoundException e) {
            throw new ItemNotFoundException(e);
        }
    }

    private void checkUpdateRights(Long userId, Long itemId) {
        if (itemId == null) {
            throw new ItemNotFoundException();
        }
        if (!Objects.equals(itemRepository.getReferenceById(itemId).getOwner().getId(), userId)) {
            throw new UserNotFoundException();
        }
    }

    @Override
    public ItemDtoWithBookings getItemByIdWithBookings(Long userId, Long itemId) {
        try {
            log.info("Получаем вещь #{} с бронированиями пользователя {}", itemId, userId);
            return ItemMapper.toItemDtoWithBookings(itemRepository.getReferenceById((itemId)),
                findLastBooking(userId, itemId), findNextBooking(userId, itemId),
                findComments(itemId));
        } catch (EntityNotFoundException e) {
            throw new ItemNotFoundException(e);
        }
    }

    private Collection<CommentDtoToReturn> findComments(Long itemId) {
        return commentsToDtos(
            commentRepository.findAllByItemId(itemId, Sort.by(Direction.DESC, "created")));
    }

    private Collection<CommentDtoToReturn> commentsToDtos(Collection<Comment> comments) {
        Collection<CommentDtoToReturn> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            commentDtos.add(CommentMapper.commentToCommentDtoToReturn(comment));
        }
        return commentDtos;
    }

    private Booking findLastBooking(Long userId, Long itemId) {
        return bookingRepository.findFirstByItem_IdAndEndIsBeforeAndStatusIs(itemId,
            LocalDateTime.now(), BookingStatus.APPROVED, userId);
    }

    private Booking findNextBooking(Long userId, Long itemId) {
        return bookingRepository.findFirstByItem_IdAndStartIsAfterAndStatusIs(itemId,
            LocalDateTime.now(), BookingStatus.APPROVED, userId);
    }

    @Override
    public List<ItemDtoWithBookings> getAllItemsOfUser(Long userId) {
        log.info("Получаем все вещи пользователя #{} с бронированиями", userId);
        Collection<Item> items = itemRepository.findAllByOwnerEquals(UserMapper.dtoToUser(
            userService.getUserById((userId))));
        List<ItemDtoWithBookings> itemDtos = new ArrayList<>();
        for (Item item : items) {
            itemDtos.add(
                ItemMapper.toItemDtoWithBookings(item, findLastBooking(userId, item.getId()),
                    findNextBooking(userId, item.getId()), findComments(item.getId())));
        }
        itemDtos.sort(Comparator.comparingLong(ItemDtoWithBookings::getId));
        return itemDtos;
    }

    @Override
    public Collection<ItemDto> getAllMatchingItems(String text) {
        log.info("Получаем все вещи, содержащие строку {}", text);
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        Collection<Item> items = itemRepository.getAllMatchingItems(text);
        Collection<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            itemDtos.add(ItemMapper.toItemDto(item));
        }
        return itemDtos;
    }

    @Override
    public CommentDtoToReturn postComment(Long userId, Long itemId, CommentDto commentDto) {
        log.info("Постим комментарий пользователя #{} о вещи #{}", userId, itemId);
        commentDto.setCreated(LocalDateTime.now());
        checkComments(userId, itemId);
        commentDto.setAuthor(userRepository.getReferenceById(userId));
        commentDto.setItem(itemRepository.getReferenceById(itemId));
        CommentDtoToReturn commentDtoToReturn = CommentMapper.commentToCommentDtoToReturn(
            commentRepository.save(CommentMapper.commentDtoToComment(commentDto)));
        log.info("Комментарий успешно опубликован");
        return commentDtoToReturn;
    }

    private void checkComments(Long userId, Long itemId) {
        if (bookingRepository.findByBooker_IdAndEndIsBefore(userId, LocalDateTime.now(),
                Sort.by(
                    Direction.DESC, "start")).stream()
            .noneMatch(x -> x.getItem().getId().equals(itemId))) {
            throw new CommentNotAllowedException(
                "Вы не можете оставлять комментарий для этой вещи");
        }
    }

}