package shareit.app.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import shareit.app.request.ItemRequest;
import shareit.app.user.User;

@Data
@AllArgsConstructor
public class Item {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;

}