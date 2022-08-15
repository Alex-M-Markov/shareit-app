package shareit.app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RequestNotFoundException extends RuntimeException {

    public RequestNotFoundException(Throwable cause) {
        super("Такой запрос не найден", cause);
    }
}