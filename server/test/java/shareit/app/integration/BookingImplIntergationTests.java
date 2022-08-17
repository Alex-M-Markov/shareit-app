package shareit.app.integration;

import java.time.LocalDateTime;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import shareit.app.booking.BookingDtoIncoming;
import shareit.app.booking.BookingDtoToReturn;
import shareit.app.booking.BookingIncomingStates;
import shareit.app.booking.BookingRepository;
import shareit.app.booking.BookingServiceImpl;
import shareit.app.booking.BookingStatus;
import shareit.app.exceptions.IllegalBookingAccess;
import shareit.app.exceptions.IllegalBookingException;
import shareit.app.item.Item;
import shareit.app.item.ItemMapper;
import shareit.app.item.ItemService;
import shareit.app.user.User;
import shareit.app.user.UserDto;
import shareit.app.user.UserServiceImpl;

@Transactional
@SpringBootTest(
    properties = "db.name=test",
    webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AllArgsConstructor(onConstructor_ = @Autowired)
public class BookingImplIntergationTests {

    private UserServiceImpl userService;

    private ItemService itemService;
    private BookingRepository bookingRepository;
    private BookingServiceImpl bookingService;


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
    private final Item item4 = new Item(4L, "Спицы", "Вязальные", true, user,
        null);
    private final BookingDtoIncoming bookingDtoIncoming = new BookingDtoIncoming(1L, start, end,
        1L, 2L, BookingStatus.WAITING);
    private final BookingDtoIncoming bookingDtoIncoming2 = new BookingDtoIncoming(2L, start, end,
        2L, 1L, BookingStatus.WAITING);
    private final BookingDtoIncoming bookingDtoIncoming3 = new BookingDtoIncoming(3L, start, end,
        3L, 2L, BookingStatus.WAITING);
    private final BookingDtoIncoming bookingDtoIncoming4 = new BookingDtoIncoming(3L, start, newEnd,
        4L, 2L, BookingStatus.APPROVED);


    @BeforeEach
    public void beforeEach() {
        bookingService = new BookingServiceImpl(bookingRepository, userService, itemService);
        userService.create(userDto);
        userService.create(userDto2);
        itemService.create(2L, ItemMapper.toItemDto(item));
        itemService.create(1L, ItemMapper.toItemDto(item2));
        itemService.create(1L, ItemMapper.toItemDto(item3));
        itemService.create(1L, ItemMapper.toItemDto(item4));
    }

    @Test
    public void create() {
        BookingDtoToReturn bookingDtoReturned = bookingService.create(2L, bookingDtoIncoming3);
        Assertions.assertEquals(bookingDtoIncoming3.getStart(), bookingDtoReturned.getStart());
        Assertions.assertEquals(bookingDtoIncoming3.getEnd(), bookingDtoReturned.getEnd());
        Assertions.assertEquals(bookingDtoIncoming3.getItemId(),
            bookingDtoReturned.getItem().getId());
        Assertions.assertEquals(bookingDtoIncoming3.getUserId(),
            bookingDtoReturned.getBooker().getId());
    }

    @Test
    public void createOwnItemError() {
        IllegalBookingAccess exception = Assertions.assertThrows(IllegalBookingAccess.class,
            () -> bookingService.create(2L, bookingDtoIncoming));
        Assertions.assertEquals("Нельзя забронировать собственную вещь", exception.getMessage());
    }

    @Test
    public void createNotAvailableItemError() {
        IllegalBookingException exception = Assertions.assertThrows(IllegalBookingException.class,
            () -> bookingService.create(2L, bookingDtoIncoming2));
        Assertions.assertEquals("Эта вещь недоступна для бронирования", exception.getMessage());
    }

    @Test
    public void update() {
        bookingService.create(2L, bookingDtoIncoming3);
        BookingDtoToReturn bookingUpdatedReceived = bookingService.update(1L, 1L, true);
        Assertions.assertEquals(bookingUpdatedReceived.getStatus(), BookingStatus.APPROVED);
    }

    @Test
    public void getBookingById() {
        bookingService.create(2L, bookingDtoIncoming3);
        BookingDtoToReturn bookingDtoReturned = bookingService.getBookingById(1L, 1L);
        Assertions.assertEquals(bookingDtoIncoming3.getStart(), bookingDtoReturned.getStart());
        Assertions.assertEquals(bookingDtoIncoming3.getEnd(), bookingDtoReturned.getEnd());
        Assertions.assertEquals(bookingDtoIncoming3.getItemId(),
            bookingDtoReturned.getItem().getId());
        Assertions.assertEquals(bookingDtoIncoming3.getUserId(),
            bookingDtoReturned.getBooker().getId());
    }


    @Test
    public void getAllBookingsOfUser() {
        bookingService.create(2L, bookingDtoIncoming3);
        bookingService.create(2L, bookingDtoIncoming4);
        Assertions.assertEquals(2,
            bookingService.getAllBookingsOfUser(2L, BookingIncomingStates.ALL,
                DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE).size());
        Assertions.assertEquals(1,
            bookingService.getAllBookingsOfUser(2L, BookingIncomingStates.ALL,
                DEFAULT_PAGE_FIRST_ELEMENT, 1).size());
        Assertions.assertEquals(2,
            bookingService.getAllBookingsOfUser(2L, BookingIncomingStates.WAITING,
                DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE).size());
        Assertions.assertEquals(0,
            bookingService.getAllBookingsOfUser(2L, BookingIncomingStates.CURRENT,
                DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE).size());
        Assertions.assertEquals(0,
            bookingService.getAllBookingsOfUser(2L, BookingIncomingStates.REJECTED,
                DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE).size());
        Assertions.assertEquals(0,
            bookingService.getAllBookingsOfUser(2L, BookingIncomingStates.PAST,
                DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE).size());
        Assertions.assertEquals(2,
            bookingService.getAllBookingsOfUser(2L, BookingIncomingStates.FUTURE,
                DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE).size());
    }

    @Test
    public void getAllBookingsOfUserItems() {
        bookingService.create(1L, bookingDtoIncoming);
        bookingService.create(2L, bookingDtoIncoming3);
        bookingService.create(2L, bookingDtoIncoming4);
        Assertions.assertEquals(2,
            bookingService.getAllBookingsOfUserItems(1L, BookingIncomingStates.ALL,
                DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE).size());
        Assertions.assertEquals(1,
            bookingService.getAllBookingsOfUserItems(2L, BookingIncomingStates.ALL,
                DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE).size());
        Assertions.assertEquals(1,
            bookingService.getAllBookingsOfUserItems(1L, BookingIncomingStates.ALL,
                DEFAULT_PAGE_FIRST_ELEMENT, 1).size());
        Assertions.assertEquals(0,
            bookingService.getAllBookingsOfUserItems(1L, BookingIncomingStates.CURRENT,
                DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE).size());
        Assertions.assertEquals(2,
            bookingService.getAllBookingsOfUserItems(1L, BookingIncomingStates.WAITING,
                DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE).size());
        Assertions.assertEquals(0,
            bookingService.getAllBookingsOfUserItems(1L, BookingIncomingStates.REJECTED,
                DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE).size());
        Assertions.assertEquals(0,
            bookingService.getAllBookingsOfUserItems(1L, BookingIncomingStates.PAST,
                DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE).size());
        Assertions.assertEquals(2,
            bookingService.getAllBookingsOfUserItems(1L, BookingIncomingStates.FUTURE,
                DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE).size());
    }
}