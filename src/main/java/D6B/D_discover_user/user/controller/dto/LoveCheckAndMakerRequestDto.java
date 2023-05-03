package D6B.D_discover_user.user.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoveCheckAndMakerRequestDto {
    private String uid;
    private Long pictureId;
    private String makerUid;
}
