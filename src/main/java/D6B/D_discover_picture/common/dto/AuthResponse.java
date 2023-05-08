package D6B.D_discover_picture.common.dto;

import com.google.firebase.auth.FirebaseToken;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private Boolean isUser;
    private FirebaseToken decodedToken;
}
