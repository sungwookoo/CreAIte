package D6B.D_discover_user.user.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@Data
public class UserPicsResponseDto {
    private Long pictureId;
    private String pictureImgSrc;
    private Instant createdAt;
}
