package D6B.D_discover_user.user.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMadeDto {
    private Long pictureId;
    private String pictureUrl;
    private String makerUid;
    private Long loveCount;
    private Instant createdAt;
    private List<String> pictureTags;
    private Boolean loveCheck;
    private Boolean isPublic;
}
