package shareit.app.unit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.annotation.DirtiesContext;
import shareit.app.booking.Booking;
import shareit.app.booking.BookingRepository;
import shareit.app.booking.BookingStatus;
import shareit.app.exceptions.ItemNotFoundException;
import shareit.app.item.Comment;
import shareit.app.item.CommentDto;
import shareit.app.item.CommentDtoToReturn;
import shareit.app.item.CommentRepository;
import shareit.app.item.Item;
import shareit.app.item.ItemDto;
import shareit.app.item.ItemDtoWithBookings;
import shareit.app.item.ItemRepository;
import shareit.app.item.ItemServiceImpl;
import shareit.app.user.User;
import shareit.app.user.UserDto;
import shareit.app.user.UserRepository;
import shareit.app.user.UserService;


@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceImplTests {

    private final ItemDto itemDto = new ItemDto(2L, "Стул", "Кожаный на колёсах",
        true, 3L);
    private final ItemDtoWithBookings itemDtoWithBookings = new ItemDtoWithBookings(2L, "Стул",
        "Кожаный на колёсах",
        true, 3L, null, null, new ArrayList<>());
    private final ItemDtoWithBookings itemDtoWithBookings2 = new ItemDtoWithBookings(3L,
        "Табуретка", "Из Икеи",
        false, null, null, null, new ArrayList<>());
    private Item item;
    private Item item2;
    private Item itemToUpdate;
    private final UserDto userDto = new UserDto(5L, "Новый пользователь",
        "user5@gmail.com");
    private final ItemDto itemDtoToUpdate = new ItemDto(2L, "Стул",
        "Кожаный на ножках", true, 3L);
    private final User user = new User(5L, "Новый пользователь", "user5@gmail.com");
    private final User user2 = new User(8L, "Восьмой", "newage@yahoo.com");

    private ItemServiceImpl itemService;
    private static final Integer DEFAULT_PAGE_FIRST_ELEMENT = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 20;

    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private CommentRepository mockCommentRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private UserService mockUserService;
    @Mock
    private BookingRepository mockBookingRepository;


    @BeforeEach
    public void beforeEach() {
        itemService = new ItemServiceImpl(mockItemRepository, mockCommentRepository,
            mockUserRepository, mockUserService, mockBookingRepository);
        item = new Item(2L, "Стул", "Кожаный на колёсах", true, user,
            3L);
        item2 = new Item(3L, "Табуретка", "Из Икеи", false, user,
            null);
    }

    @Test
    public void create() {
        Mockito
            .when(mockItemRepository.save(item))
            .thenReturn(item);
        Mockito
            .when(mockUserService.getUserById(5L))
            .thenReturn(userDto);

        Assertions.assertEquals(itemDto, itemService.create(5L, itemDto));
    }

    @Test
    public void update() {
        itemToUpdate = new Item(2L, "Стул", "Кожаный на ножках", true,
            user, 3L);

        Mockito
            .when(mockItemRepository.save(itemToUpdate))
            .thenReturn(itemToUpdate);
        Mockito
            .when(mockItemRepository.getReferenceById(2L))
            .thenReturn(item);
        Mockito
            .when(mockUserService.getUserById(5L))
            .thenReturn(userDto);

        Assertions.assertEquals(itemDtoToUpdate, itemService.update(5L, 2L,
            itemDtoToUpdate));
    }

    @Test
    public void updateItemNotFound() {
        itemToUpdate = new Item(2L, "Стул", "Кожаный на ножках", true,
            user, 3L);

        Mockito
            .when(mockItemRepository.getReferenceById(8L))
            .thenThrow(new ItemNotFoundException());

        Assertions.assertThrows(ItemNotFoundException.class,
            () -> itemService.update(5L, 8L, itemDtoToUpdate));
    }

    @Test
    public void getItemById() {
        Mockito
            .when(mockItemRepository.getReferenceById(2L))
            .thenReturn(item);

        Assertions.assertEquals(itemDto, itemService.getItemById(2L));
    }

    @Test
    public void getItemByIdWithBookings() {
        Mockito
            .when(mockItemRepository.getReferenceById(2L))
            .thenReturn(item);

        Assertions.assertEquals(itemDtoWithBookings, itemService.getItemByIdWithBookings(5L, 2L));
    }


    @Test
    public void getAllItemsOfUser() {
        List<Item> twoItemsList = List.of(item, item2);
        Page<Item> pageItems = new PageImpl<>(twoItemsList);
        List<ItemDtoWithBookings> twoItemsDtoList = List.of(itemDtoWithBookings,
            itemDtoWithBookings2);
        Sort sort = Sort.by(Direction.ASC, "id");

        Mockito
            .when(mockItemRepository.findAllByOwnerEquals(user,
                PageRequest.of(DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE, sort)))
            .thenReturn(pageItems);
        Mockito
            .when(mockUserService.getUserById(5L))
            .thenReturn(userDto);

        Assertions.assertEquals(twoItemsDtoList,
            itemService.getAllItemsOfUser(5L, DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE));
    }


    @Test
    public void getAllMatchingItems() {
        List<Item> matchingItemList = List.of(item);
        Page<Item> pageItems = new PageImpl<>(matchingItemList);
        List<ItemDto> matchingItemDtoList = List.of(itemDto);
        Sort sort = Sort.by(Direction.ASC, "id");

        Mockito
            .when(mockItemRepository.getAllMatchingItems("кожаный",
                PageRequest.of(DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE, sort)))
            .thenReturn(pageItems);

        Assertions.assertEquals(matchingItemDtoList, itemService.getAllMatchingItems("кожаный",
            DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE));
    }

    @Test
    public void postComment() {
        Sort sort = Sort.by(Direction.DESC, "start");
        LocalDateTime start = LocalDateTime.of(2022, 3, 5, 12, 5);
        LocalDateTime end = LocalDateTime.of(2022, 5, 5, 12, 5);
        LocalDateTime actual = LocalDateTime.of(2022, 7, 5, 12, 5);

        Comment comment = new Comment(1L, "Мой первый комментарий", item, user2,
            actual);
        CommentDto commentDto = new CommentDto(1L, "Мой первый комментарий", item, user2,
            actual);
        CommentDtoToReturn commentDtoToReturn = new CommentDtoToReturn(1L, "Мой первый комментарий",
            "Восьмой", actual);

        Booking booking = new Booking(3L, start, end, item, user2, BookingStatus.APPROVED);
        Page<Booking> bookingPaged = new PageImpl<>(List.of(booking));

        Mockito
            .when(mockUserRepository.getReferenceById(8L))
            .thenReturn(user2);
        Mockito
            .when(mockItemRepository.getReferenceById(2L))
            .thenReturn(item);
        Mockito
            .when(mockCommentRepository.save(comment))
            .thenReturn(comment);
        Mockito
            .when(mockBookingRepository.findByBookerIdAndEndIsBefore(8L, actual,
                PageRequest.of(DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE, sort)))
            .thenReturn(bookingPaged);

        Assertions.assertEquals(commentDtoToReturn, itemService.postComment(8L, 2L, commentDto));
    }

}