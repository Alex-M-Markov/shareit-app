package shareit.app.unit;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import shareit.app.exceptions.UserNotFoundException;
import shareit.app.user.User;
import shareit.app.user.UserDto;
import shareit.app.user.UserRepository;
import shareit.app.user.UserServiceImpl;


@ExtendWith(MockitoExtension.class)
public class UserServiceImplTests {

    private final UserDto userDto = new UserDto(5L, "Новый пользователь", "user5@gmail.com");
    private final UserDto userDto2 = new UserDto(8L, "Восьмой", "newage@yahoo.com");
    private final UserDto userDtoForUpdate = new UserDto(5L, "Новый пользователь",
        "Updated5@gmail.com");
    private final User user = new User(5L, "Новый пользователь", "user5@gmail.com");
    private final User user2 = new User(8L, "Восьмой", "newage@yahoo.com");
    private final User userForUpdate = new User(5L, "Новый пользователь", "Updated5@gmail.com");
    private UserServiceImpl userService;

    @Mock
    private UserRepository mockUserRepository;

    @BeforeEach
    public void beforeEach() {
        userService = new UserServiceImpl(mockUserRepository);
    }

    @Test
    public void create() {
        Mockito
            .when(mockUserRepository.save(user))
            .thenReturn(user);

        Assertions.assertEquals(userDto, userService.create(userDto));
    }

    @Test
    public void update() {
        Mockito
            .when(mockUserRepository.save(userForUpdate))
            .thenReturn(userForUpdate);
        Mockito
            .when(mockUserRepository.getReferenceById(5L))
            .thenReturn(user);

        Assertions.assertEquals(userDtoForUpdate, userService.update(5L, userDtoForUpdate));
    }

    @Test
    public void updateUserNotFound() {
        Mockito
            .when(mockUserRepository.getReferenceById(4L))
            .thenThrow(new UserNotFoundException());

        Assertions.assertThrows(UserNotFoundException.class,
            () -> userService.update(4L, userDtoForUpdate));
    }

    @Test
    public void getUserById() {
        Mockito
            .when(mockUserRepository.getReferenceById(5L))
            .thenReturn(user);

        Assertions.assertEquals(userDto, userService.getUserById(5L));
    }

    @Test
    public void getAllUsers() {
        List<User> twoUsersList = List.of(user, user2);
        List<UserDto> twoUsersDtoList = List.of(userDto, userDto2);

        Mockito
            .when(mockUserRepository.findAll())
            .thenReturn(twoUsersList);

        Assertions.assertEquals(twoUsersDtoList, userService.getAllUsers());
    }


}