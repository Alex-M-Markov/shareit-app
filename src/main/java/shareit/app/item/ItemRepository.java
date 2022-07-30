package shareit.app.item;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shareit.app.user.User;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerEquals(User user);

    @Query("select i from Item i " +
        "where upper(i.name) like upper(concat('%', ?1, '%')) " +
        "or upper(i.description) like upper(concat('%', ?1, '%'))" +
        "and i.available is true ")
    Collection<Item> getAllMatchingItems(String text);

}