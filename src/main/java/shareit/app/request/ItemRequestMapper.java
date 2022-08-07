package shareit.app.request;

import shareit.app.user.User;

public class ItemRequestMapper {

    public static ItemRequest dtoToItemRequest(ItemRequestDto itemRequestDto, User user) {
        return new ItemRequest(itemRequestDto.getId(), itemRequestDto.getDescription(),
            new User(user.getId(), user.getName(), user.getEmail()), itemRequestDto.getCreated());
    }

    public static ItemRequestDto itemRequestToDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto(itemRequest.getId(),
            itemRequest.getDescription(), new ItemRequestDto.User(), itemRequest.getCreated());
        itemRequestDto.getRequestor().setId(itemRequest.getRequestor().getId());
        itemRequestDto.getRequestor().setName(itemRequest.getRequestor().getName());
        itemRequestDto.getRequestor().setEmail(itemRequest.getRequestor().getEmail());
        return itemRequestDto;
    }

}