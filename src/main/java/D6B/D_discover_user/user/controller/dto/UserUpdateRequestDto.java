package D6B.D_discover_user.user.controller.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserUpdateRequestDto {
    private final String uid;
    private final String name;
    private final String gender;
    private final Integer age;
}