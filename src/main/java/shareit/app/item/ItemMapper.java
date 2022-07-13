package shareit.app.item;

import lombok.Data;
import shareit.app.user.UserDto;
import shareit.app.user.UserMapper;

@Data
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
            item.getRequest()
        );
    }

    public static Item dtoToItem(ItemDto itemDto, UserDto user) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(),
            itemDto.getAvailable(), UserMapper.dtoToUser(user), itemDto.getRequest());
    }

}