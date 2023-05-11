package D6B.D_discover_user.user.controller.dto;

import D6B.D_discover_user.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponseDto {

    private String name;
    private String profileImg;
    private String gender;
    private Integer age;
    private Instant createdAt;


    public static UserInfoResponseDto from(User user) {
        return UserInfoResponseDto.builder()
                .name(user.getName())
                .profileImg(user.getProfileImg())
                .gender(user.getGender())
                .age(user.getAge())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
