package D6B.D_discover_user.user.controller.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserUpdateRequestDto {

    private final String gender;
    private final Integer age;
    private final String mobileNumber;  // 번호 인증...?
}