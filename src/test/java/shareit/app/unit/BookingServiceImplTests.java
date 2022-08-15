package shareit.app.unit;

import java.time.LocalDateTime;
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
import shareit.app.booking.Booking;
import shareit.app.booking.BookingDtoIncoming;
import shareit.app.booking.BookingDtoToReturn;
import shareit.app.booking.BookingDtoToReturn.Booker;
import shareit.app.booking.BookingIncomingStates;
import shareit.app.booking.BookingRepository;
import shareit.app.booking.BookingServiceImpl;
import shareit.app.booking.BookingStatus;
import shareit.app.exceptions.BookingNotFoundException;
import shareit.app.item.Item;
import shareit.app.item.ItemDto;
import shareit.app.item.ItemService;
import shareit.app.user.User;
import shareit.app.user.UserDto;
import shareit.app.user.UserService;


@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTests {

    private static final Integer DEFAULT_PAGE_FIRST_ELEMENT = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 20;

    private final ItemDto itemDto = new ItemDto(2L, "Стул", "Кожаный на колёсах",
        true, 3L);
    private Item item;
    private final UserDto userDto = new UserDto(5L, "Новый пользователь",
        "user5@gmail.com");
    private final UserDto userDto2 = new UserDto(8L, "Восьмой", "newage@yahoo.com");

    private final User user = new User(5L, "Новый пользователь", "user5@gmail.com");
    private final User user2 = new User(8L, "Восьмой", "newage@yahoo.com");
    private Booking booking;
    private Booking booking2;
    private Booking bookingToUpdate;
    private BookingDtoIncoming bookingDtoIncoming;
    private BookingDtoToReturn bookingDtoToReturn;
    private BookingDtoToReturn bookingDtoToReturn2;

    private BookingServiceImpl bookingService;
    private final LocalDateTime start = LocalDateTime.of(2022, 9, 5, 12, 5);
    private final LocalDateTime end = LocalDateTime.of(2022, 10, 5, 12, 5);
    private final LocalDateTime newEnd = LocalDateTime.of(2023, 1, 5, 12, 5);

    @Mock
    private ItemService mockItemService;
    @Mock
    private UserService mockUserService;
    @Mock
    private BookingRepository mockBookingRepository;


    @BeforeEach
    public void beforeEach() {
        bookingService = new BookingServiceImpl(mockBookingRepository, mockUserService,
            mockItemService);
        item = new Item(2L, "Стул", "Кожаный на колёсах", true, user2,
            3L);
        Item item2 = new Item(3L, "Табуретка", "Из Икеи", false, user,
            null);

        booking = new Booking(10L, start, end, item, user2, BookingStatus.WAITING);
        booking2 = new Booking(14L, start, newEnd, item2, user2, BookingStatus.WAITING);
        bookingDtoToReturn = new BookingDtoToReturn(10L, start, end,
            new BookingDtoToReturn.Item(2L, "Стул"), new Booker(8L, "Восьмой"),
            BookingStatus.WAITING);
        bookingDtoToReturn2 = new BookingDtoToReturn(14L, start, newEnd,
            new BookingDtoToReturn.Item(3L, "Табуретка"), new Booker(8L, "Восьмой"),
            BookingStatus.WAITING);

        bookingDtoIncoming = new BookingDtoIncoming(10L, start, end, 2L, 5L,
            BookingStatus.WAITING);

    }

    @Test
    public void create() {
        Mockito
            .when(mockBookingRepository.save(booking))
            .thenReturn(booking);
        Mockito
            .when(mockUserService.getUserById(8L))
            .thenReturn(userDto2);
        Mockito
            .when(mockItemService.getItemById(2L))
            .thenReturn(itemDto);

        Assertions.assertEquals(bookingDtoToReturn, bookingService.create(8L, bookingDtoIncoming));
    }


    @Test
    public void update() {
        bookingToUpdate = new Booking(10L, start, newEnd, item, user2, BookingStatus.REJECTED);
        BookingDtoToReturn bookingDtoToReturnUpdated = new BookingDtoToReturn(10L, start, newEnd,
            new BookingDtoToReturn.Item(2L, "Стул"), new Booker(8L, "Восьмой"),
            BookingStatus.REJECTED);

        Mockito
            .when(mockBookingRepository.save(bookingToUpdate))
            .thenReturn(bookingToUpdate);
        Mockito
            .when(mockBookingRepository.getReferenceById(10L))
            .thenReturn(bookingToUpdate);

        Assertions.assertEquals(bookingDtoToReturnUpdated, bookingService.update(8L, 10L, false));
    }


    @Test
    public void updateBookingNotFound() {
        bookingToUpdate = new Booking(10L, start, end, item, user2, BookingStatus.REJECTED);

        Mockito
            .when(mockBookingRepository.getReferenceById(3L))
            .thenThrow(new BookingNotFoundException(new Throwable()));

        Assertions.assertThrows(BookingNotFoundException.class,
            () -> bookingService.update(5L, 3L, false));
    }

    @Test
    public void getBookingById() {
        Mockito
            .when(mockBookingRepository.getReferenceById(12L))
            .thenReturn(booking);

        Assertions.assertEquals(bookingDtoToReturn, bookingService.getBookingById(8L, 12L));
    }

    @Test
    public void getAllBookingsOfUser() {
        List<Booking> twoBookingsList = List.of(booking, booking2);
        Page<Booking> pageBookings = new PageImpl<>(twoBookingsList);
        List<BookingDtoToReturn> twoBookingsDtoToReturnList = List.of(bookingDtoToReturn,
            bookingDtoToReturn2);
        Sort sort = Sort.by(Direction.DESC, "start");

        Mockito
            .when(mockBookingRepository.findByBookerId(8L,
                PageRequest.of(DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE, sort)))
            .thenReturn(pageBookings);
        Mockito
            .when(mockUserService.getUserById(8L))
            .thenReturn(userDto2);

        Assertions.assertEquals(twoBookingsDtoToReturnList,
            bookingService.getAllBookingsOfUser(8L, BookingIncomingStates.ALL,
                DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE));
    }


    @Test
    public void getAllBookingsOfUserItems() {
        Item item3 = new Item(4L, "Чемодан", "Хороший, новый", true, user,
            2L);
        Booking booking3 = new Booking(11L, start, newEnd, item3, user2, BookingStatus.WAITING);
        BookingDtoToReturn bookingDtoToReturn3 = new BookingDtoToReturn(11L, start, newEnd,
            new BookingDtoToReturn.Item(4L, "Чемодан"), new Booker(8L, "Восьмой"),
            BookingStatus.WAITING);

        List<Booking> matchingBookingsList = List.of(booking2, booking3);
        Page<Booking> pageBookings = new PageImpl<>(matchingBookingsList);
        List<BookingDtoToReturn> matchingBookingDtoList = List.of(bookingDtoToReturn2,
            bookingDtoToReturn3);
        Sort sort = Sort.by(Direction.DESC, "start");

        Mockito
            .when(mockUserService.getUserById(5L))
            .thenReturn(userDto);
        Mockito
            .when(mockBookingRepository.findAllByItemOwnerId(5L,
                PageRequest.of(DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE, sort)))
            .thenReturn(pageBookings);

        Assertions.assertEquals(matchingBookingDtoList,
            bookingService.getAllBookingsOfUserItems(5L, BookingIncomingStates.ALL,
                DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE));
    }

}