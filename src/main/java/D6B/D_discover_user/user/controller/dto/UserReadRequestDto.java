package D6B.D_discover_user.user.controller.dto;

import D6B.D_discover_user.user.domain.User;
import lombok.Data;

/**
 * Firebase를 통해 구글 로그인했을 때 받을 수 있는 정보
 */
@Data
public class UserReadRequestDto {
    private final String uid;
    private final String email;
    private final String name;
    private final String img_src;

    public static User to(UserReadRequestDto userRequestDto, Long userId) {
        return User.builder()
                .uid(userRequestDto.getUid())
                .email(userRequestDto.getEmail())
                .name(userRequestDto.getEmail())
                .imgSrc(userRequestDto.getImg_src())
                .id(userId)
                .build();
    }
}
