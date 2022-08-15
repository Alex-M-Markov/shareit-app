package shareit.app.jpa;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import shareit.app.booking.Booking;
import shareit.app.booking.BookingRepository;
import shareit.app.booking.BookingStatus;
import shareit.app.item.Item;
import shareit.app.item.ItemRepository;
import shareit.app.user.User;
import shareit.app.user.UserRepository;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestPropertySource(locations = "classpath:application.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TestBookingRepository {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;


    private final LocalDateTime start = LocalDateTime.of(2022, 7, 5, 12, 5);
    private final LocalDateTime newStart = LocalDateTime.of(2022, 12, 4, 1, 12);
    private final LocalDateTime end = LocalDateTime.of(2022, 8, 5, 12, 5);
    private final LocalDateTime newEnd = LocalDateTime.of(2023, 1, 5, 12, 5);


    @Test
    void injectedComponentsAreNotNull() {

        Assertions.assertNotNull(em);
        Assertions.assertNotNull(bookingRepository);
        Assertions.assertNotNull(userRepository);
        Assertions.assertNotNull(itemRepository);
    }

    @Test
    void findFirstByItemIdAndStartIsAfterAndStatusIs() {
        User user = new User(null, "Самый первый пользователь", "first@first.ca");
        User user2 = new User(null, "Восьмой", "newage@yahoo.com");
        em.persist(user);
        em.persist(user2);

        Item item = new Item(null, "Мышка", "Беспроводная", true, user, null);
        Item item2 = new Item(null, "Табуретка", "Из Икеи", false, user, null);
        em.persist(item);
        em.persist(item2);

        Booking booking = new Booking(null, start, end, item, user2, BookingStatus.WAITING);
        Booking booking2 = new Booking(null, newStart, newEnd, item2, user2,
            BookingStatus.APPROVED);
        Booking booking3 = new Booking(null, newStart, newEnd, item, user, BookingStatus.WAITING);
        Booking booking4 = new Booking(null, start, end, item2, user, BookingStatus.APPROVED);

        em.persist(booking);
        em.persist(booking2);
        em.persist(booking3);
        em.persist(booking4);

        Booking result = bookingRepository.findFirstByItemIdAndStartIsAfterAndStatusIs(2L,
            LocalDateTime.now(), BookingStatus.APPROVED, 1L);
        Assertions.assertEquals(result, booking2);
    }

    @Test
    void findFirstByItemIdAndEndIsBeforeAndStatusIs() {
        User user = new User(null, "Самый первый пользователь", "first@first.ca");
        User user2 = new User(null, "Восьмой", "newage@yahoo.com");
        em.persist(user);
        em.persist(user2);

        Item item = new Item(null, "Мышка", "Беспроводная", true, user, null);
        Item item2 = new Item(null, "Табуретка", "Из Икеи", false, user, null);
        em.persist(item);
        em.persist(item2);

        Booking booking = new Booking(null, start, end, item, user2, BookingStatus.WAITING);
        Booking booking2 = new Booking(null, newStart, newEnd, item2, user2,
            BookingStatus.APPROVED);
        Booking booking3 = new Booking(null, newStart, newEnd, item, user, BookingStatus.WAITING);
        Booking booking4 = new Booking(null, start, end, item2, user, BookingStatus.APPROVED);

        em.persist(booking);
        em.persist(booking2);
        em.persist(booking3);
        em.persist(booking4);

        Booking result = bookingRepository.findFirstByItemIdAndEndIsBeforeAndStatusIs(2L,
            LocalDateTime.now(), BookingStatus.APPROVED, 1L);
        Assertions.assertEquals(result, booking4);
    }

}