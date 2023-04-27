package D6B.D_discover_user.user.controller.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoveReadRequestDto {
    private String uid;
    private Long picture_id;
}
