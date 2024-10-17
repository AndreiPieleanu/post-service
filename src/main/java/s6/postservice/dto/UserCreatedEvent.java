package s6.postservice.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserCreatedEvent {
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    // Any other relevant fields.
}

