package shareit.app.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import shareit.app.exceptions.ItemNotFoundException;
import shareit.app.exceptions.UserNotFoundException;
import shareit.app.user.UserService;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserService userService;


    public ItemDto create(Long userId, ItemDto item) {
        checkUserExistence(userId);
        return ItemMapper.toItemDto(
            itemStorage.create(ItemMapper.dtoToItem(item, userService.getUserById(userId))));
    }

    private void checkUserExistence(Long userId) {
        if (userService.getUserById(userId) == null) {
            throw new UserNotFoundException();
        }
    }

    public ItemDto update(Long userId, Long itemId, ItemDto item) {
        checkUserExistence(userId);
        checkUpdateRights(userId, itemId);
        ItemDto itemToUpdate = updateItemFields(itemId, item);
        return ItemMapper.toItemDto(itemStorage.update(
            ItemMapper.dtoToItem(itemToUpdate, userService.getUserById(userId))));
    }

    private ItemDto updateItemFields(Long id, ItemDto item) {
        ItemDto itemToUpdate = getItemById(id);
        if (itemToUpdate == null) {
            throw new ItemNotFoundException();
        }
        if (item.getName() != null) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }
        return itemToUpdate;
    }

    private void checkUpdateRights(Long userId, Long itemId) {
        if (itemId == null) {
            throw new ItemNotFoundException();
        }
        if (!Objects.equals(itemStorage.getItemById(itemId).getOwner().getId(), userId)) {
            throw new UserNotFoundException();
        }
    }

    public ItemDto getItemById(Long itemId) {
        return ItemMapper.toItemDto(itemStorage.getItemById(itemId));
    }

    public Collection<ItemDto> getAllItemsOfUser(Long userId) {
        Collection<Item> items = itemStorage.getAllItemsOfUser(userId);
        Collection<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            itemDtos.add(ItemMapper.toItemDto(item));
        }
        return itemDtos;
    }

    public Collection<ItemDto> getAllMatchingItems(String text) {
        Collection<Item> items = itemStorage.getAllMatchingItems(text);
        Collection<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            itemDtos.add(ItemMapper.toItemDto(item));
        }
        return itemDtos;
    }

}