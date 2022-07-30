package shareit.app.item;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentDtoToReturn {

    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;

}