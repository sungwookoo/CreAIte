package D6B.D_discover_user.user.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Builder
@Data
public class UserPicsResponseDto {
    private Long pictureId;
    private String pictureUrl;
    private String makerUid;
    private Long loveCount;
    private Instant createdAt;
    private List<String> pictureTags;
    private Boolean loveCheck;
    private String makerName;
}
