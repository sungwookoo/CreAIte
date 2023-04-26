package D6B.D_discover_user.user.controller.dto;

import D6B.D_discover_user.user.domain.User;
import lombok.Data;

/**
 * Firebase를 통해 구글 로그인했을 때 받을 수 있는 정보
 */
@Data
public class UserRequestDto {
    private final String g_id;
    private final String g_mail;
    private final String g_name;
    private final String img_src;

    public static User to(UserRequestDto userRequestDto, Long userId) {
        return User.builder()
                .googleId(userRequestDto.g_id)
                .gmail(userRequestDto.g_mail)
                .nickname(userRequestDto.g_name)
                .imgSrc(userRequestDto.img_src)
                .id(userId)
                .build();
    }
}
