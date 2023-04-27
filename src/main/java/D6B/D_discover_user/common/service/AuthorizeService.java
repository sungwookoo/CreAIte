package D6B.D_discover_user.common.service;

import D6B.D_discover_user.common.dto.AuthResponse;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

//import static D6B.D_discover_user.common.ConstValues.*;

@Slf4j
@Service
public class AuthorizeService {
    // 토큰과 uid 검증
    // 회원인증
    public AuthResponse isAuthorized(String idToken, String uid) throws IOException, FirebaseAuthException {
        // Firebase 초기화
        if(FirebaseApp.getApps().isEmpty()) {
            FileInputStream serviceAccount = new FileInputStream("creaite-app-firebase-adminsdk.json");
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
        }
        // Firebase 토큰 디코드
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        String decodedTokenUid = decodedToken.getUid();
        // uid 변형이 없었는지 검증
        if(Objects.equals(decodedTokenUid, uid)) {
            return AuthResponse.builder()
                    .isUser(true)
                    .decodedToken(decodedToken)
                    .build();
        } else {
            return AuthResponse.builder()
                    .isUser(false)
                    .decodedToken(null)
                    .build();
        }
    }
}
