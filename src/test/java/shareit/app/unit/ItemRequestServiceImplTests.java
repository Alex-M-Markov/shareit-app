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
import shareit.app.item.ItemRepository;
import shareit.app.request.ItemRequest;
import shareit.app.request.ItemRequestDto;
import shareit.app.request.ItemRequestDtoWithAnswers;
import shareit.app.request.ItemRequestRepository;
import shareit.app.request.ItemRequestServiceImpl;
import shareit.app.user.User;
import shareit.app.user.UserRepository;
import shareit.app.user.UserService;


@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTests {

    private static final Integer DEFAULT_PAGE_FIRST_ELEMENT = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 20;
    private final User user = new User(5L, "Новый пользователь", "user5@gmail.com");
    private final User user2 = new User(8L, "Восьмой", "newage@yahoo.com");
    private ItemRequest itemRequest;
    private ItemRequest itemRequest2;
    private ItemRequest itemRequest3;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDtoWithAnswers itemRequestDtoWithAnswers;
    private ItemRequestDtoWithAnswers itemRequestDtoWithAnswers2;
    private ItemRequestDtoWithAnswers itemRequestDtoWithAnswers3;
    private ItemRequestServiceImpl itemRequestService;
    private final LocalDateTime start = LocalDateTime.of(2022, 9, 5, 12, 5);
    private final LocalDateTime end = LocalDateTime.of(2022, 10, 5, 12, 5);

    @Mock
    private UserService mockUserService;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private ItemRequestRepository mockItemRequestRepository;


    @BeforeEach
    public void beforeEach() {
        itemRequestService = new ItemRequestServiceImpl(mockItemRequestRepository,
            mockUserRepository, mockUserService, mockItemRepository);
        itemRequest = new ItemRequest(1L, "Хочу собаку", user, start);
        itemRequest2 = new ItemRequest(2L, "Хочу кошку", user, end);
        itemRequest3 = new ItemRequest(4L, "Погонять бы PS-3", user2, end);
        itemRequestDto = new ItemRequestDto(1L, "Хочу собаку",
            new ItemRequestDto.User(5L, "Новый пользователь", "user5@gmail.com"), start);
        itemRequestDtoWithAnswers = new ItemRequestDtoWithAnswers(1L, "Хочу собаку",
            new ItemRequestDtoWithAnswers.User(5L, "Новый пользователь", "user5@gmail.com"),
            new ArrayList<>(), start);
        itemRequestDtoWithAnswers2 = new ItemRequestDtoWithAnswers(2L, "Хочу кошку",
            new ItemRequestDtoWithAnswers.User(5L, "Новый пользователь", "user5@gmail.com"),
            new ArrayList<>(), end);
        itemRequestDtoWithAnswers3 = new ItemRequestDtoWithAnswers(4L, "Погонять бы PS-3",
            new ItemRequestDtoWithAnswers.User(8L, "Восьмой", "newage@yahoo.com"),
            new ArrayList<>(), end);

    }

    @Test
    public void create() {
        Mockito
            .when(mockItemRequestRepository.save(itemRequest))
            .thenReturn(itemRequest);
        Mockito
            .when(mockUserRepository.getReferenceById(5L))
            .thenReturn(user);

        Assertions.assertEquals(itemRequestDto, itemRequestService.create(5L, itemRequestDto));
    }

    @Test
    public void getRequestById() {
        Mockito
            .when(mockItemRequestRepository.getReferenceById(2L))
            .thenReturn(itemRequest2);

        Assertions.assertEquals(itemRequestDtoWithAnswers2,
            itemRequestService.getRequestById(5L, 2L));
    }

    @Test
    public void getAllRequestsOfUser() {
        List<ItemRequest> twoRequestsList = List.of(itemRequest, itemRequest2);
        List<ItemRequestDtoWithAnswers> twoRequestsDtoToReturnList = List.of(
            itemRequestDtoWithAnswers, itemRequestDtoWithAnswers2);
        Sort sort = Sort.by(Direction.DESC, "created");

        Mockito
            .when(mockItemRequestRepository.findByRequestorId(5L, sort))
            .thenReturn(twoRequestsList);

        Assertions.assertEquals(twoRequestsDtoToReturnList,
            itemRequestService.getAllRequestsOfUser(5L));
    }

    @Test
    public void getAllRequestsOfOtherUsers() {
        List<ItemRequest> requestsOfOtherUsers = List.of(itemRequest3);
        List<ItemRequestDtoWithAnswers> requestsDtoOfOtherUsers = List.of(
            itemRequestDtoWithAnswers3);
        Sort sort = Sort.by(Direction.DESC, "created");
        Page<ItemRequest> pageRequests = new PageImpl<>(requestsOfOtherUsers);

        Mockito
            .when(mockItemRequestRepository.findAll(
                PageRequest.of(DEFAULT_PAGE_FIRST_ELEMENT, DEFAULT_PAGE_SIZE, sort)))
            .thenReturn(pageRequests);

        Assertions.assertEquals(requestsDtoOfOtherUsers,
            itemRequestService.getAllRequestsOfOtherUsers(5L, DEFAULT_PAGE_FIRST_ELEMENT,
                DEFAULT_PAGE_SIZE));
    }

}