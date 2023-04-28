package D6B.D_discover_user.user.controller.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoveToggleRequestDto {
    private String uid;
    private Long pictureId;
    private String makerUid;
}
