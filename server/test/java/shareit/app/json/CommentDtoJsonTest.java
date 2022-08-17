package shareit.app.json;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import shareit.app.item.CommentDto;
import shareit.app.item.Item;
import shareit.app.user.User;

@JsonTest
public class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testCommentDto() throws Exception {

        CommentDto commentDto = new CommentDto(
            1L,
            "Пожалуй, больше брать не буду",
            new Item(3L, "Соковыжималка", "Ручная",
                true,
                new User(8L, "Восьмой", "newage@yahoo.com"),
                null),
            new User(1L, "Новый пользователь", "user5@gmail.com"),
            LocalDateTime.of(2020, 5, 2, 0, 10, 5));

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text")
            .isEqualTo("Пожалуй, больше брать не буду");
        assertThat(result).extractingJsonPathStringValue("$.created")
            .isEqualTo("2020-05-02T00:10:05");
    }

}
