package shareit.app.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Long, User> users = new HashMap<>();
    private static Long id = 1L;

    public User create(User user) {
        if (user.getId() == null) {
            user.setId(id);
            id++;
        }
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public void deleteUserById(Long id) {
        users.remove(id);
    }

    public User getUserById(Long id) {
        return users.get(id);
    }

    public Collection<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

}