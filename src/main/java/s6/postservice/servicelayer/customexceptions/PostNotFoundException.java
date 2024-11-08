package s6.postservice.servicelayer.customexceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PostNotFoundException extends ResponseStatusException {
    public PostNotFoundException() {
        super(HttpStatus.NOT_FOUND, "POST COULD NOT BE FOUND");
    }
}
