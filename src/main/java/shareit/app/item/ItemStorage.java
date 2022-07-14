package shareit.app.item;

import java.util.Collection;

public interface ItemStorage {

    Item create(Item item);

    Item update(Item item);

    Item getItemById(Long itemId);

    Collection<Item> getAllItemsOfUser(Long userId);

    Collection<Item> getAllMatchingItems(String text);
}