package s6.postservice.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PostUpdatedEvent {
    private Integer id;
    private String text;
    private Integer userId;
    private Boolean isBlocked;
}
