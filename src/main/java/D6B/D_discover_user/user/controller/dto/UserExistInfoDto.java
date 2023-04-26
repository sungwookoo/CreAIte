package D6B.D_discover_user.user.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserExistInfoDto {
    private final Long user_id;

    public static UserExistInfoDto from(Long userId) {
        return UserExistInfoDto.builder()
                .user_id(userId)
                .build();
    }
}
