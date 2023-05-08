package D6B.D_discover_picture.common.service;

import D6B.D_discover_picture.common.dto.AuthResponse;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

@Service
public class AuthorizeService {
    public AuthResponse isAuthorized(String idToken, String uid) throws IOException, FirebaseAuthException {
        if(FirebaseApp.getApps().isEmpty()) {
            FileInputStream serviceAccount = new FileInputStream("creaite-app-firebase-adminsdk.json");
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
        }

        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        String decodedTokenUid = decodedToken.getUid();
        if (Objects.equals(decodedTokenUid, uid)) {
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
