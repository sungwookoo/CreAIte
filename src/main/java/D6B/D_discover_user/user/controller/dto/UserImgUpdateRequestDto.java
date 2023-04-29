package D6B.D_discover_user.user.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserImgUpdateRequestDto {
    private String uid;
    private String profileImg;
}
