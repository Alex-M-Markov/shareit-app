package ru.practicum.shareit.user;

import ru.practicum.shareit.exceptions.UserNotFoundException;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User dtoToUser(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }

}