package shareit.app.jpa;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import shareit.app.item.Item;
import shareit.app.item.ItemRepository;
import shareit.app.user.User;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestPropertySource(locations = "classpath:application.properties")
public class TestItemRepository {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;


    @Test
    void injectedComponentsAreNotNull() {

        Assertions.assertNotNull(em);
        Assertions.assertNotNull(itemRepository);
    }

    @Test
    void getAllMatchingItems() {

        User user = new User(null, "Самый первый пользователь", "first@first.ca");
        Item item = new Item(null, "Мышка", "Беспроводная", true, user, null);
        em.persist(user);
        em.persist(item);

        Page<Item> result = itemRepository.getAllMatchingItems("ыш",
            PageRequest.of(0, 10, Sort.unsorted()));
        List<Item> listOfResults = result.getContent();
        Assertions.assertEquals(listOfResults.size(), 1);
    }

    @Test
    void getNoMatchingItems() {

        User user = new User(null, "Самый первый пользователь", "first@first.ca");
        Item item = new Item(null, "Мышка", "Беспроводная", true, user, null);
        em.persist(user);
        em.persist(item);

        Page<Item> result = itemRepository.getAllMatchingItems("Куртка",
            PageRequest.of(0, 10, Sort.unsorted()));
        List<Item> listOfResults = result.getContent();
        Assertions.assertEquals(listOfResults.size(), 0);
    }

}