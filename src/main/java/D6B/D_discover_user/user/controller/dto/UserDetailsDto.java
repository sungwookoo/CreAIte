package D6B.D_discover_user.user.controller.dto;

import D6B.D_discover_user.user.domain.User;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@Data
public class UserDetailsDto {
    // 유저의 이메일
    private String gmail;
    // 유저의 닉네임
    private String nickname;
    // 유저의 이미지소스
    private String img_src;
    // 유저의 성별
    private String gender;
    // 유저의 나이
    private Integer age;
    // 유저의 가입일
    private Instant created_at;
    // 유저의 핸드폰 번호->핸드폰 인증?
    private String mobile_number;

    public static UserDetailsDto from(User user) {
        return UserDetailsDto.builder()
                .gmail(user.getGmail())
                .nickname(user.getNickname())
                .img_src(user.getImgSrc())
                .gender(user.getGender())
                .age(user.getAge())
                .created_at(user.getCreatedAt())
                .mobile_number(user.getMobileNumber())
                .build();
    }
}
