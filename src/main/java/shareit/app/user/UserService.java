package shareit.app.user;

import java.util.Collection;

public interface UserService {

    UserDto create(UserDto user);

    UserDto update(Long id, UserDto user);

    void deleteUserById(Long id);

    UserDto getUserById(Long id);

    Collection<UserDto> getAllUsers();

}