package shareit.app.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String name;
    @NotNull
    @Email(message = "Вы ввели некорректный e-mail")
    private String email;
}