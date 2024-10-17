package s6.postservice.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PostCreatedEvent {
    private Integer id;
    private String text;
    private Boolean isBlocked;
}
