package D6B.D_discover_user.user.controller.dto;

import D6B.D_discover_user.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Firebase를 통해 구글 로그인했을 때 받을 수 있는 정보
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReadRequestDto {
    private String uid;
    private String email;
    private String name;
    private String profileImg;
    private String fcmToken;
}
