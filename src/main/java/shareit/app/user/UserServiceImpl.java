package shareit.app.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import shareit.app.exceptions.IllegalInputException;
import shareit.app.exceptions.UserNotFoundException;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;


    public UserDto create(UserDto user) {
        checkMailInput(user);
        return UserMapper.toUserDto(userStorage.create(UserMapper.dtoToUser(user)));
    }

    public UserDto update(Long id, UserDto user) {
        UserDto userToUpdate = updateUserFields(id, user);
        return UserMapper.toUserDto(userStorage.update(UserMapper.dtoToUser(userToUpdate)));
    }

    private UserDto updateUserFields(Long id, UserDto user) {
        UserDto userToUpdate = getUserById(id);
        if (userToUpdate == null) {
            throw new UserNotFoundException();
        }
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            checkMailInput(user);
            userToUpdate.setEmail(user.getEmail());
        }
        return userToUpdate;
    }

    public void deleteUserById(Long id) {
        userStorage.deleteUserById(id);
    }

    public UserDto getUserById(Long id) {
        return UserMapper.toUserDto(userStorage.getUserById(id));
    }

    public Collection<UserDto> getAllUsers() {
        ArrayList<UserDto> allUsers = new ArrayList<>();
        for (User user : userStorage.getAllUsers()) {
            allUsers.add(UserMapper.toUserDto(user));
        }
        return allUsers;
    }

    private void checkMailInput(UserDto user) {
        String email = user.getEmail();
        for (User registeredUser : userStorage.getAllUsers()) {
            if (email.equals(registeredUser.getEmail()) && !Objects.equals(user.getId(),
                registeredUser.getId())) {
                throw new IllegalInputException("???????????????????????? ?? ?????????? E-mail ?????? ????????????????????");
            }
        }
    }

}