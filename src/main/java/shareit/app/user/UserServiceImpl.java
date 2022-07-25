package shareit.app.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shareit.app.exceptions.IllegalInputException;
import shareit.app.exceptions.UserNotFoundException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto user) {
        return UserMapper.toUserDto(userRepository.save((UserMapper.dtoToUser(user))));
    }

    @Override
    public UserDto update(Long id, UserDto user) {
        UserDto userToUpdate = updateUserFields(id, user);
        return UserMapper.toUserDto(userRepository.save((UserMapper.dtoToUser(userToUpdate))));
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

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDto getUserById(Long id) {
        try {
            return UserMapper.toUserDto(userRepository.getReferenceById(id));
        } catch (EntityNotFoundException e) {
            throw new UserNotFoundException();
        }
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        ArrayList<UserDto> allUsers = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            allUsers.add(UserMapper.toUserDto(user));
        }
        return allUsers;
    }

    private void checkMailInput(UserDto user) {
        String email = user.getEmail();
        for (User registeredUser : userRepository.findAll()) {
            if (email.equals(registeredUser.getEmail()) && !Objects.equals(user.getId(),
                registeredUser.getId())) {
                throw new IllegalInputException("Пользователь с таким E-mail уже существует");
            }
        }
    }

}