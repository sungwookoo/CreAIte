package D6B.D_discover_user.user.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoveCheckAndMakerResponseDto {
    private Boolean loveCheck;
    private String makerName;
}
