package shareit.app.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import shareit.app.user.User;

@Data
@AllArgsConstructor
public class ItemRequest {

    private Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;

}