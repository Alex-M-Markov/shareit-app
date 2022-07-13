package shareit.app.user;

import java.util.Collection;

public interface UserStorage {

    User create(User user);

    User update(User user);

    void deleteUserById(Long id);

    User getUserById(Long id);

    Collection<User> getAllUsers();

}