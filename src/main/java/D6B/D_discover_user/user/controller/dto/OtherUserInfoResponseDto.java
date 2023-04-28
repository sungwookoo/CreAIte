package D6B.D_discover_user.user.controller.dto;

import D6B.D_discover_user.user.domain.User;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@Data
public class OtherUserInfoResponseDto {
    private String name;
    private String img_src;
    private String gender;
    private Integer age;
    private Instant created_at;

    public static OtherUserInfoResponseDto from(User user) {
        return OtherUserInfoResponseDto.builder()
                .name(user.getName())
                .img_src(user.getImgSrc())
                .gender(user.getGender())
                .age(user.getAge())
                .created_at(user.getCreatedAt())
                .build();
    }
}
