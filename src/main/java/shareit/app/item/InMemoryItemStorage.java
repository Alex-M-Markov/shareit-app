package shareit.app.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryItemStorage implements ItemStorage {

    private final HashMap<Long, Item> items = new HashMap<>();
    private static Long id = 1L;

    @Override
    public Item create(Item item) {
        if (item.getId() == null) {
            item.setId(id);
            id++;
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItemById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public Collection<Item> getAllItemsOfUser(Long userId) {
        return items.values().stream()
            .filter(x -> x.getOwner().getId().equals(userId))
            .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> getAllMatchingItems(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        return items.values().stream()
            .filter(x -> x.getAvailable().equals(true))
            .filter(
                x -> x.getName().toLowerCase().contains(text.toLowerCase()) || x.getDescription()
                    .toLowerCase().contains(text.toLowerCase()))
            .collect(Collectors.toList());
    }

}