package D6B.D_discover_user.user.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Builder
@Data
public class UserPicsResponseDto {
    private String pictureUrl;
    private Long pictureId;
    private String makerUId;
    private String makerName;
    private Long loveCount;
    private Instant createdAt;
    private List<String> imgTags;
    private Boolean loveCheck;
}
