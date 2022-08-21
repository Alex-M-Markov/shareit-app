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
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import shareit.app.user.UserController;
import shareit.app.user.UserDto;
import shareit.app.user.UserServiceImpl;

@WebMvcTest(UserController.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserServiceImpl mockUserService;
    @Autowired
    private ObjectMapper mapper;
    private final UserDto userDto = new UserDto(
        1L,
        "Новый пользователь",
        "user5@gmail.com");

    private final UserDto userDto2 = new UserDto(
        2L,
        "Второй",
        "newage@yahoo.com");

    private final UserDto userDtoUpdated = new UserDto(
        1L,
        "Уже не новый пользователь",
        "user5@gmail.com");

    @BeforeEach
    void setUp(WebApplicationContext wac) {
        mvc = MockMvcBuilders
            .webAppContextSetup(wac)
            .build();
    }

    @Test
    void create() throws Exception {
        when(mockUserService.create(any()))
            .thenReturn(userDto);

        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(userDto.getName())))
            .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void update() throws Exception {
        when(mockUserService.update(anyLong(), any()))
            .thenReturn(userDtoUpdated);

        mvc.perform(patch("/users/1")
                .content(mapper.writeValueAsString(userDtoUpdated))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(userDtoUpdated.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(userDtoUpdated.getName())))
            .andExpect(jsonPath("$.email", is(userDtoUpdated.getEmail())));
    }

    @Test
    void deleteUserById() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/users/1"))
            .andExpect(status().isOk());
    }

    @Test
    void getUserById() throws Exception {
        when(mockUserService.getUserById(anyLong()))
            .thenReturn(userDto);

        mvc.perform(get("/users/1")
                .content(mapper.writeValueAsString(userDtoUpdated))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(userDto.getName())))
            .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void getAllUsers() throws Exception {
        when(mockUserService.getAllUsers())
            .thenReturn(List.of(userDto, userDto2));

        mvc.perform(get("/users")
                .content(mapper.writeValueAsString(List.of(userDto, userDto2)))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

}