package D6B.D_discover_user.user.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDto {
    private String uid;
    private String name;
    private String gender;
    private Integer age;
}