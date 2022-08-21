package shareit.app.integration;

import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import shareit.app.user.UserDto;
import shareit.app.user.UserRepository;
import shareit.app.user.UserServiceImpl;

@Transactional
@SpringBootTest(
    properties = "db.name=test",
    webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AllArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplIntergationTests {

    private final UserDto userDto = new UserDto(1L, "Новый пользователь", "user5@gmail.com");
    private final UserDto userDto2 = new UserDto(2L, "Восьмой", "newage@yahoo.com");
    private final UserDto userDtoForUpdate = new UserDto(1L, "Новый пользователь",
        "Updated5@gmail.com");
    private UserServiceImpl userService;

    private UserRepository userRepository;

    @BeforeEach
    public void beforeEach() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    public void create() {
        UserDto userReturned = userService.create(userDto);
        Assertions.assertEquals(userReturned, userDto);
    }

    @Test
    public void update() {
        userService.create(userDto);
        UserDto updatedUserReturned = userService.update(1L, userDtoForUpdate);
        Assertions.assertEquals(updatedUserReturned, userDtoForUpdate);
    }

    @Test
    public void deleteUserById() {
        userService.create(userDto);
        userService.deleteUserById(1L);

        Assertions.assertEquals(0, userService.getAllUsers().size());
    }

    @Test
    public void getUserById() {
        userService.create(userDto);
        Assertions.assertEquals(userDto, userService.getUserById(1L));
    }

    @Test
    public void getAllUsers() {
        userService.create(userDto);
        userService.create(userDto2);

        Assertions.assertEquals(2, userService.getAllUsers().size());
    }

}