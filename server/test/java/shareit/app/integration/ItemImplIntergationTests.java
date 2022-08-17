package shareit.app.integration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import shareit.app.booking.Booking;
import shareit.app.booking.BookingRepository;
import shareit.app.booking.BookingStatus;
import shareit.app.item.CommentDto;
import shareit.app.item.CommentDtoToReturn;
import shareit.app.item.CommentRepository;
import shareit.app.item.Item;
import shareit.app.item.ItemDto;
import shareit.app.item.ItemMapper;
import shareit.app.item.ItemRepository;
import shareit.app.item.ItemService;
import shareit.app.item.ItemServiceImpl;
import shareit.app.user.User;
import shareit.app.user.UserDto;
import shareit.app.user.UserRepository;
import shareit.app.user.UserServiceImpl;

@Transactional
@SpringBootTest(
    properties = "db.name=test",
    webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ItemImplIntergationTests {

    private UserServiceImpl userService;
    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private CommentRepository commentRepository;
    private UserRepository userRepository;
    private ItemService itemService;


    private final LocalDateTime start = LocalDateTime.of(2022, 9, 5, 12, 5);
    private final LocalDateTime end = LocalDateTime.of(2022, 10, 5, 12, 5);
    private final LocalDateTime newEnd = LocalDateTime.of(2023, 1, 5, 12, 5);
    private final User user = new User(1L, "Новый пользователь", "user5@gmail.com");
    private final User user2 = new User(2L, "Восьмой", "newage@yahoo.com");
    private final UserDto userDto = new UserDto(1L, "Новый пользователь", "user5@gmail.com");
    private final UserDto userDto2 = new UserDto(2L, "Восьмой", "newage@yahoo.com");
    private static final Integer DEFAULT_PAGE_FIRST_ELEMENT = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 20;
    private final Item item = new Item(1L, "Стул", "Кожаный на колёсах", true, user2,
        null);
    private final Item item2 = new Item(2L, "Табуретка", "Из Икеи", false, user,
        null);
    private final Item item3 = new Item(3L, "Щетка", "С длинным ворсом", true, user,
        null);
    private final Item item3ToUpdate = new Item(3L, "Щетка деревянная", "С коротким ворсом", false,
        user, null);
    private final Booking booking = new Booking(1L, start, end, item, user2,
        BookingStatus.WAITING);
    private final Booking booking2 = new Booking(2L, start, newEnd, item2, user2,
        BookingStatus.WAITING);


    @BeforeEach
    public void beforeEach() {
        itemService = new ItemServiceImpl(itemRepository, commentRepository, userRepository,
            userService, bookingRepository);
        userService.create(userDto);
        userService.create(userDto2);
    }

    @Test
    public void create() {
        ItemDto itemDtoReturned = itemService.create(2L, ItemMapper.toItemDto(item));
        Assertions.assertEquals(item.getName(), itemDtoReturned.getName());
        Assertions.assertEquals(item.getDescription(), itemDtoReturned.getDescription());
        Assertions.assertEquals(item.getAvailable(), itemDtoReturned.getAvailable());
    }

    @Test
    public void update() {
        itemService.create(2L, ItemMapper.toItemDto(item));
        itemService.create(1L, ItemMapper.toItemDto(item2));
        itemService.create(1L, ItemMapper.toItemDto(item3));
        ItemDto item3Updated = itemService.update(1L, 3L, ItemMapper.toItemDto(item3ToUpdate));
        Assertions.assertEquals(item3ToUpdate.getName(), item3Updated.getName());
        Assertions.assertEquals(item3ToUpdate.getDescription(), item3Updated.getDescription());
        Assertions.assertEquals(item3ToUpdate.getAvailable(), item3Updated.getAvailable());
    }

    @Test
    public void getItemById() {
        itemService.create(2L, ItemMapper.toItemDto(item));
        itemService.create(1L, ItemMapper.toItemDto(item2));
        itemService.create(1L, ItemMapper.toItemDto(item3));
        Assertions.assertEquals(item3.getName(), itemService.getItemById(3L).getName());
        Assertions.assertEquals(item3.getDescription(),
            itemService.getItemById(3L).getDescription());
        Assertions.assertEquals(item3.getAvailable(), itemService.getItemById(3L).getAvailable());
    }

    @Test
    public void getItemByIdWithBookings() {
        Booking booking3 = new Booking(2L, newEnd, LocalDateTime.of(2024, 1, 2, 3, 4), item2, user2,
            BookingStatus.APPROVED);
        itemService.create(2L, ItemMapper.toItemDto(item));
        itemService.create(1L, ItemMapper.toItemDto(item2));
        itemService.create(1L, ItemMapper.toItemDto(item3));
        bookingRepository.save(booking);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
        Assertions.assertEquals(2,
            itemService.getItemByIdWithBookings(1L, 2L).getNextBooking().getId());
        Assertions.assertEquals(2,
            itemService.getItemByIdWithBookings(1L, 2L).getNextBooking().getBookerId());
        Assertions.assertNull(itemService.getItemByIdWithBookings(1L, 2L).getLastBooking());
    }

    @Test
    public void getAllItemsOfUser() {
        itemService.create(2L, ItemMapper.toItemDto(item));
        itemService.create(1L, ItemMapper.toItemDto(item2));
        itemService.create(1L, ItemMapper.toItemDto(item3));
        Assertions.assertEquals(2,
            itemService.getAllItemsOfUser(1L, DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE)
                .size());
        Assertions.assertEquals(2,
            itemService.getAllItemsOfUser(1L, null, null).size());
        Assertions.assertEquals(1,
            itemService.getAllItemsOfUser(2L, DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE)
                .size());
        Assertions.assertEquals(1,
            itemService.getAllItemsOfUser(1L, DEFAULT_PAGE_FIRST_ELEMENT, 1).size());
    }

    @Test
    public void getAllMatchingItemsOfUser() {
        itemService.create(2L, ItemMapper.toItemDto(item));
        itemService.create(1L, ItemMapper.toItemDto(item2));
        itemService.create(1L, ItemMapper.toItemDto(item3));
        Assertions.assertEquals(new ArrayList<>(),
            itemService.getAllMatchingItems("Машина", null, null));
        Assertions.assertEquals(2, itemService.getAllMatchingItems("тка", null, null).size());
        Assertions.assertEquals(1,
            itemService.getAllMatchingItems("табурет", DEFAULT_PAGE_FIRST_ELEMENT,
                DEFAULT_PAGE_SIZE).size());
    }

    @Test
    public void postComment() {
        itemService.create(2L, ItemMapper.toItemDto(item));
        itemService.create(1L, ItemMapper.toItemDto(item2));
        itemService.create(1L, ItemMapper.toItemDto(item3));
        Booking booking3 = new Booking(2L, LocalDateTime.of(2021, 1, 1, 1, 1),
            LocalDateTime.of(2022, 2, 2, 2, 2), item2, user2,
            BookingStatus.APPROVED);
        bookingRepository.save(booking);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
        CommentDto comment = new CommentDto(1L, "Супер, очень понравилось", item2, user2,
            LocalDateTime.now());
        CommentDtoToReturn commentReturned = itemService.postComment(2L, 2L, comment);
        Assertions.assertEquals("Супер, очень понравилось", commentReturned.getText());
    }

}