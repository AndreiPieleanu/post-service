package s6.postservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostResponse {
    private Integer id;
    private String text;
    private Integer userId;
    private Boolean isBlocked;
}
