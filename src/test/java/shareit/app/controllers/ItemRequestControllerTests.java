package shareit.app.controllers;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import shareit.app.request.ItemRequestController;
import shareit.app.request.ItemRequestDto;
import shareit.app.request.ItemRequestDtoWithAnswers;
import shareit.app.request.ItemRequestServiceImpl;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTests {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemRequestServiceImpl mockItemRequestService;
    @Autowired
    private ObjectMapper mapper;

    private final LocalDateTime start = LocalDateTime.of(2022, 9, 5, 12, 5);
    private final LocalDateTime end = LocalDateTime.of(2022, 10, 5, 12, 5);
    private static final Integer DEFAULT_PAGE_FIRST_ELEMENT = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 20;

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Хочу собаку",
        new ItemRequestDto.User(1L, "Новый пользователь", "user5@gmail.com"), start);
    private final ItemRequestDtoWithAnswers itemRequestDtoWithAnswers = new ItemRequestDtoWithAnswers(
        1L,
        "Хочу собаку", new ItemRequestDtoWithAnswers.User(2L, "Восьмой", "newage@yahoo.com"),
        new ArrayList<>(), start);
    private final ItemRequestDtoWithAnswers itemRequestDtoWithAnswers2 = new ItemRequestDtoWithAnswers(
        2L,
        "Хочу кошку", new ItemRequestDtoWithAnswers.User(2L, "Восьмой", "newage@yahoo.com"),
        new ArrayList<>(), end);

    @BeforeEach
    void setUp(WebApplicationContext wac) {
        mvc = MockMvcBuilders
            .webAppContextSetup(wac)
            .build();
    }

    @Test
    void create() throws Exception {
        when(mockItemRequestService.create(anyLong(), any()))
            .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                .content(mapper.writeValueAsString(itemRequestDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .header("X-Sharer-User-Id", 8L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
            .andExpect(
                jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    void getAllRequestsOfUser() throws Exception {
        when(mockItemRequestService.getAllRequestsOfUser(2L))
            .thenReturn(List.of(itemRequestDtoWithAnswers, itemRequestDtoWithAnswers2));

        mvc.perform(get("/requests")
                .content(mapper.writeValueAsString(
                    List.of(itemRequestDtoWithAnswers, itemRequestDtoWithAnswers2)))
                .characterEncoding(StandardCharsets.UTF_8)
                .header("X-Sharer-User-Id", 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void getAllRequestsOfOtherUsers() throws Exception {
        when(mockItemRequestService.getAllRequestsOfOtherUsers(1L, DEFAULT_PAGE_FIRST_ELEMENT,
            DEFAULT_PAGE_SIZE))
            .thenReturn(List.of(itemRequestDtoWithAnswers, itemRequestDtoWithAnswers2));

        mvc.perform(get("/requests/all")
                .content(mapper.writeValueAsString(
                    List.of(itemRequestDtoWithAnswers, itemRequestDtoWithAnswers2)))
                .characterEncoding(StandardCharsets.UTF_8)
                .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void getRequestById() throws Exception {
        when(mockItemRequestService.getRequestById(anyLong(), anyLong()))
            .thenReturn(itemRequestDtoWithAnswers);

        mvc.perform(get("/requests/2")
                .content(mapper.writeValueAsString(itemRequestDtoWithAnswers))
                .characterEncoding(StandardCharsets.UTF_8)
                .header("X-Sharer-User-Id", 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(itemRequestDtoWithAnswers.getId()), Long.class))
            .andExpect(
                jsonPath("$.description", is(itemRequestDtoWithAnswers.getDescription())));
    }

}