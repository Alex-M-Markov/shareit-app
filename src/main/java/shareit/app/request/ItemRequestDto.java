package shareit.app.request;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {

    private Long id;
    @NotNull
    @NotBlank
    private String description;
    private User requestor;
    private LocalDateTime created;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {

        protected Long id;
        protected String name;
        protected String email;
    }
}