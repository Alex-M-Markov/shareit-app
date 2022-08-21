package shareit.app.integration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import shareit.app.item.ItemRepository;
import shareit.app.request.ItemRequestDto;
import shareit.app.request.ItemRequestDtoWithAnswers;
import shareit.app.request.ItemRequestRepository;
import shareit.app.request.ItemRequestServiceImpl;
import shareit.app.user.UserDto;
import shareit.app.user.UserRepository;
import shareit.app.user.UserServiceImpl;

@Transactional
@SpringBootTest(
    properties = "db.name=test",
    webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestImplIntergationTests {

    private ItemRequestServiceImpl itemRequestService;
    private UserServiceImpl userService;
    private ItemRequestRepository itemRequestRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private final LocalDateTime start = LocalDateTime.of(2022, 9, 5, 12, 5);
    private final LocalDateTime end = LocalDateTime.of(2022, 10, 5, 12, 5);
    private final UserDto userDto = new UserDto(1L, "Новый пользователь", "user5@gmail.com");
    private final UserDto userDto2 = new UserDto(2L, "Восьмой", "newage@yahoo.com");
    private static final Integer DEFAULT_PAGE_FIRST_ELEMENT = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 20;


    @BeforeEach
    public void beforeEach() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository,
            userService, itemRepository);
        userService.create(userDto);
        userService.create(userDto2);
    }

    @Test
    public void create() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Хочу собаку",
            new ItemRequestDto.User(1L, "Новый пользователь", "user5@gmail.com"), start);
        ItemRequestDto itemReturned = itemRequestService.create(1L, itemRequestDto);
        Assertions.assertEquals(itemReturned, itemRequestDto);
    }

    @Test
    public void getRequestById() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Хочу собаку",
            new ItemRequestDto.User(1L, "Новый пользователь", "user5@gmail.com"), start);
        ItemRequestDtoWithAnswers itemRequestDtoWithAnswers = new ItemRequestDtoWithAnswers(1L,
            "Хочу собаку",
            new ItemRequestDtoWithAnswers.User(1L, "Новый пользователь", "user5@gmail.com"),
            new ArrayList<>(), start);
        itemRequestService.create(1L, itemRequestDto);
        Assertions.assertEquals(itemRequestDtoWithAnswers,
            itemRequestService.getRequestById(1L, 1L));
    }

    @Test
    public void getAllRequestsOfUser() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Хочу собаку",
            new ItemRequestDto.User(2L, "Восьмой", "newage@yahoo.com"),
            start);
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(2L, "Хочу кошку",
            new ItemRequestDto.User(2L, "Восьмой", "newage@yahoo.com"), end);
        ItemRequestDtoWithAnswers itemRequestDtoWithAnswers = new ItemRequestDtoWithAnswers(1L,
            "Хочу собаку", new ItemRequestDtoWithAnswers.User(2L, "Восьмой", "newage@yahoo.com"),
            new ArrayList<>(), start);
        ItemRequestDtoWithAnswers itemRequestDtoWithAnswers2 = new ItemRequestDtoWithAnswers(2L,
            "Хочу кошку", new ItemRequestDtoWithAnswers.User(2L, "Восьмой", "newage@yahoo.com"),
            new ArrayList<>(), end);
        List<ItemRequestDtoWithAnswers> twoRequestsDtoToReturnList = List.of(
            itemRequestDtoWithAnswers2, itemRequestDtoWithAnswers);
        itemRequestService.create(2L, itemRequestDto);
        itemRequestService.create(2L, itemRequestDto2);
        Assertions.assertEquals(2, itemRequestService.getAllRequestsOfUser(2L).size());
        Assertions.assertEquals(twoRequestsDtoToReturnList,
            itemRequestService.getAllRequestsOfUser(2L));
    }

    @Test
    public void getAllRequestsOfOtherUsers() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Хочу собаку",
            new ItemRequestDto.User(1L, "Новый пользователь", "user5@gmail.com"),
            start);
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(2L, "Хочу кошку",
            new ItemRequestDto.User(2L, "Новый пользователь", "user5@gmail.com"), end);
            ItemRequestDtoWithAnswers itemRequestDtoWithAnswers2 = new ItemRequestDtoWithAnswers(2L,
            "Хочу кошку",
            new ItemRequestDtoWithAnswers.User(2L, "Восьмой", "newage@yahoo.com"),
            new ArrayList<>(), end);
        List<ItemRequestDtoWithAnswers> requestsDtoToReturnList = List.of(
            itemRequestDtoWithAnswers2);
        itemRequestService.create(1L, itemRequestDto);
        itemRequestService.create(2L, itemRequestDto2);
        Assertions.assertEquals(1,
            itemRequestService.getAllRequestsOfOtherUsers(2L, DEFAULT_PAGE_FIRST_ELEMENT,
                DEFAULT_PAGE_SIZE).size());
        Assertions.assertEquals(requestsDtoToReturnList,
            itemRequestService.getAllRequestsOfUser(2L));
    }

}