package ru.practicum.shareit.item;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.User;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findAllByOwnerEquals(User user, Pageable pageable);

    List<Item> findAllByRequestIdEquals(Long requestId);

    @Query("select i from Item i " +
        "where upper(i.name) like upper(concat('%', ?1, '%')) " +
        "or upper(i.description) like upper(concat('%', ?1, '%'))" +
        "and i.available is true ")
    Page<Item> getAllMatchingItems(String text, Pageable pageable);

}