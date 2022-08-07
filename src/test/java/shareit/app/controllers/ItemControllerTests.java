package shareit.app.controllers;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
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
import shareit.app.item.CommentDto;
import shareit.app.item.CommentDtoToReturn;
import shareit.app.item.Item;
import shareit.app.item.ItemController;
import shareit.app.item.ItemDto;
import shareit.app.item.ItemDtoWithBookings;
import shareit.app.item.ItemServiceImpl;
import shareit.app.user.User;

@WebMvcTest(ItemController.class)
public class ItemControllerTests {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemServiceImpl mockItemService;
    @Autowired
    private ObjectMapper mapper;

    private final LocalDateTime start = LocalDateTime.of(2022, 9, 5, 12, 5);
    private static final Integer DEFAULT_PAGE_FIRST_ELEMENT = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 20;
    private final User user = new User(1L, "Новый пользователь", "user5@gmail.com");
    private final User user2 = new User(2L, "Восьмой", "newage@yahoo.com");
    private final Item item2 = new Item(2L, "Табуретка", "Из Икеи", false, user,
        null);
    private final ItemDto itemDto = new ItemDto(2L, "Стул", "Кожаный на колёсах",
        true, 3L);
    private final ItemDtoWithBookings itemDtoWithBookings = new ItemDtoWithBookings(2L, "Стул",
        "Кожаный на колёсах",
        true, 3L, null, null, new ArrayList<>());
    private final ItemDtoWithBookings itemDtoWithBookings2 = new ItemDtoWithBookings(3L,
        "Табуретка", "Из Икеи",
        false, null, null, null, new ArrayList<>());
    private final CommentDto comment = new CommentDto(1L, "Супер, очень понравилось", item2, user2,
        LocalDateTime.now());
    CommentDtoToReturn commentDtoToReturn = new CommentDtoToReturn(1L, "Супер, очень понравилось",
        "Восьмой", start);

    @BeforeEach
    void setUp(WebApplicationContext wac) {
        mvc = MockMvcBuilders
            .webAppContextSetup(wac)
            .build();
    }

    @Test
    void create() throws Exception {
        when(mockItemService.create(anyLong(), any()))
            .thenReturn(itemDto);

        mvc.perform(post("/items")
                .content(mapper.writeValueAsString(itemDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
            .andExpect(
                jsonPath("$.name", is(itemDto.getName())))
            .andExpect(
                jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void update() throws Exception {
        when(mockItemService.update(anyLong(), anyLong(), any()))
            .thenReturn(itemDto);

        mvc.perform(patch("/items/2")
                .content(mapper.writeValueAsString(itemDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .header("X-Sharer-User-Id", 1L)
                .param("approved", "true")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
            .andExpect(
                jsonPath("$.name", is(itemDto.getName())))
            .andExpect(
                jsonPath("$.available", is(itemDto.getAvailable())));
    }


    @Test
    void getItemByIdWithBookings() throws Exception {
        when(mockItemService.getItemByIdWithBookings(anyLong(), anyLong()))
            .thenReturn(itemDtoWithBookings);

        mvc.perform(get("/items/2")
                .content(mapper.writeValueAsString(itemDtoWithBookings))
                .characterEncoding(StandardCharsets.UTF_8)
                .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.name", is(itemDtoWithBookings.getName())))
            .andExpect(
                jsonPath("$.available", is(itemDtoWithBookings.getAvailable())));
    }


    @Test
    void getAllItemsOfUser() throws Exception {
        when(mockItemService.getAllItemsOfUser(1L, DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE))
            .thenReturn(List.of(itemDtoWithBookings, itemDtoWithBookings2));

        mvc.perform(get("/items")
                .content(mapper.writeValueAsString(List.of(itemDtoWithBookings, itemDtoWithBookings2)))
                .characterEncoding(StandardCharsets.UTF_8)
                .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void getAllMatchingItems() throws Exception {
        when(mockItemService.getAllMatchingItems("тул", DEFAULT_PAGE_FIRST_ELEMENT,
            DEFAULT_PAGE_SIZE))
            .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                .content(mapper.writeValueAsString(List.of(itemDto)))
                .characterEncoding(StandardCharsets.UTF_8)
                .header("X-Sharer-User-Id", 2L)
                .param("text", "тул")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void postComment() throws Exception {
        when(mockItemService.postComment(anyLong(), anyLong(), any()))
            .thenReturn(commentDtoToReturn);

        mvc.perform(post("/items/1/comment")
                .content(mapper.writeValueAsString(comment))
                .characterEncoding(StandardCharsets.UTF_8)
                .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
            .andExpect(jsonPath("$.text", is(comment.getText())));
    }

}