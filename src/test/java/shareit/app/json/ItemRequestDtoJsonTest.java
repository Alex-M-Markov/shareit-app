package shareit.app.json;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import shareit.app.request.ItemRequestDto;

@JsonTest
public class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDto() throws Exception {

        ItemRequestDto itemRequestDto = new ItemRequestDto(
            1L,
            "Шуба норковая",
            new ItemRequestDto.User(1L, "Новый пользователь", "user5@gmail.com"),
            LocalDateTime.of(2022, 7, 3, 20, 12, 0));

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
            .isEqualTo("Шуба норковая");
        assertThat(result).extractingJsonPathStringValue("$.created")
            .isEqualTo("2022-07-03T20:12:00");

    }

}
