package ru.practicum.shareit.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import javax.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.IllegalInputException;
import ru.practicum.shareit.exceptions.UserNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
@Data
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto user) {
        log.info("Создается пользователь {}", user.getName());
        UserDto userDto = UserMapper.toUserDto(userRepository.save((UserMapper.dtoToUser(user))));
        log.info("Пользователь {} успешно создан", userDto.getName());
        return userDto;
    }

    @Override
    public UserDto update(Long id, UserDto user) {
        log.info("Обновляется пользователь {}", user.getName());
        UserDto userToUpdate = updateUserFields(id, user);
        UserDto userToReturn = UserMapper.toUserDto(
            userRepository.save((UserMapper.dtoToUser(userToUpdate))));
        log.info("Пользователь {} успешно обновлен", userToReturn.getName());
        return userToReturn;
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
        log.info("Удаляется пользователь #{}", id);
        userRepository.deleteById(id);
        log.info("Пользователь {} успешно удален", id);
    }

    @Override
    public UserDto getUserById(Long id) {
        try {
            log.info("Получаем пользователя #{}", id);
            return UserMapper.toUserDto(userRepository.getReferenceById(id));
        } catch (EntityNotFoundException e) {
            throw new UserNotFoundException(e);
        }
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        log.info("Получаем список всех пользователей");
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