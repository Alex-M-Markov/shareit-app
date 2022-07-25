package shareit.app.item;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import shareit.app.user.User;

@Data
@AllArgsConstructor
public class CommentDto {

    private Long id;
    @NotNull
    @NotBlank
    private String text;
    private Item item;
    private User author;
    LocalDateTime created;

}

