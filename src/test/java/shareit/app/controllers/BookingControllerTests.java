package shareit.app.controllers;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import shareit.app.booking.BookingController;
import shareit.app.booking.BookingDtoToReturn;
import shareit.app.booking.BookingDtoToReturn.Booker;
import shareit.app.booking.BookingIncomingStates;
import shareit.app.booking.BookingServiceImpl;
import shareit.app.booking.BookingStatus;

@WebMvcTest(BookingController.class)
public class BookingControllerTests {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private BookingServiceImpl mockBookingService;
    @Autowired
    private ObjectMapper mapper;

    private final LocalDateTime start = LocalDateTime.of(2022, 9, 5, 12, 5);
    private final LocalDateTime end = LocalDateTime.of(2022, 10, 5, 12, 5);
    private final LocalDateTime newEnd = LocalDateTime.of(2023, 1, 5, 12, 5);
    private static final Integer DEFAULT_PAGE_FIRST_ELEMENT = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 20;
    BookingDtoToReturn bookingDtoToReturn = new BookingDtoToReturn(10L, start, end,
        new BookingDtoToReturn.Item(2L, "Стул"), new Booker(8L, "Восьмой"),
        BookingStatus.WAITING);
    BookingDtoToReturn bookingDtoToReturn2 = new BookingDtoToReturn(14L, start, newEnd,
        new BookingDtoToReturn.Item(3L, "Табуретка"), new Booker(8L, "Восьмой"),
        BookingStatus.WAITING);
    BookingDtoToReturn bookingDtoToReturnUpdated = new BookingDtoToReturn(10L, start, newEnd,
        new BookingDtoToReturn.Item(2L, "Стул"), new Booker(8L, "Восьмой"),
        BookingStatus.REJECTED);

    @BeforeEach
    void setUp(WebApplicationContext wac) {
        mvc = MockMvcBuilders
            .webAppContextSetup(wac)
            .build();
    }

    @Test
    void create() throws Exception {
        when(mockBookingService.create(anyLong(), any()))
            .thenReturn(bookingDtoToReturn);

        mvc.perform(post("/bookings")
                .content(mapper.writeValueAsString(bookingDtoToReturn))
                .characterEncoding(StandardCharsets.UTF_8)
                .header("X-Sharer-User-Id", 8L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(bookingDtoToReturn.getId()), Long.class))
            .andExpect(
                jsonPath("$.booker.id", is(bookingDtoToReturn.getBooker().getId()), Long.class));
    }

    @Test
    void update() throws Exception {
        when(mockBookingService.update(anyLong(), anyLong(), anyBoolean()))
            .thenReturn(bookingDtoToReturnUpdated);

        mvc.perform(patch("/bookings/2")
                .content(mapper.writeValueAsString(bookingDtoToReturnUpdated))
                .characterEncoding(StandardCharsets.UTF_8)
                .header("X-Sharer-User-Id", 8L)
                .param("approved", "true")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(bookingDtoToReturnUpdated.getId()), Long.class))
            .andExpect(
                jsonPath("$.item.name", is(bookingDtoToReturnUpdated.getItem().getName())));
    }

    @Test
    void getBookingById() throws Exception {
        when(mockBookingService.getBookingById(anyLong(), anyLong()))
            .thenReturn(bookingDtoToReturn);

        mvc.perform(get("/bookings/1")
                .content(mapper.writeValueAsString(bookingDtoToReturn))
                .characterEncoding(StandardCharsets.UTF_8)
                .header("X-Sharer-User-Id", 8L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(bookingDtoToReturn.getId()), Long.class))
            .andExpect(
                jsonPath("$.booker.id", is(bookingDtoToReturn.getBooker().getId()), Long.class));
    }

    @Test
    void getAllBookings() throws Exception {
        when(mockBookingService.getAllBookingsOfUser(8L, BookingIncomingStates.ALL,
            DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE))
            .thenReturn(List.of(bookingDtoToReturn, bookingDtoToReturn2));

        mvc.perform(get("/bookings")
                .content(mapper.writeValueAsString(List.of(bookingDtoToReturn, bookingDtoToReturn2)))
                .characterEncoding(StandardCharsets.UTF_8)
                .header("X-Sharer-User-Id", 8L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void getAllBookingsOfUserItems() throws Exception {
        when(mockBookingService.getAllBookingsOfUserItems(8L, BookingIncomingStates.ALL,
            DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE))
            .thenReturn(List.of(bookingDtoToReturn, bookingDtoToReturn2));

        mvc.perform(get("/bookings/8")
                .content(mapper.writeValueAsString(List.of(bookingDtoToReturn, bookingDtoToReturn2)))
                .characterEncoding(StandardCharsets.UTF_8)
                .header("X-Sharer-User-Id", 8L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

}