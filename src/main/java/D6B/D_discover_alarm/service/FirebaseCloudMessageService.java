package D6B.D_discover_alarm.service;

import D6B.D_discover_alarm.controller.dto.FcmMessage;
import D6B.D_discover_alarm.controller.dto.NotificationDto;
import D6B.D_discover_alarm.service.exceptions.Client4xxException;
import D6B.D_discover_alarm.service.exceptions.Client5xxException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Arrays;

import static D6B.D_discover_alarm.common.ConstValues.FCM_API_URL;
import static D6B.D_discover_alarm.common.ConstValues.USER_SERVER_CLIENT;

@Component
@RequiredArgsConstructor
@Service
@Slf4j
public class FirebaseCloudMessageService {

    private static final String FIREBASE_CONFIG_PATH = "/etc/secrets/firebase/firebase_service_key.json";
    private final ObjectMapper objectMapper;
    OkHttpClient client = new OkHttpClient();
    public void sendMessageTo(String targetToken, String title, String body, String image) throws IOException {
        String message = makeMessage(targetToken, title, body, image);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message,
                MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(FCM_API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();

        System.out.println(response.body().string());
    }
    public void sendMessageTo(NotificationDto dto) throws IOException {
        // 해당 유저의 토큰값 가져오기.

//        String fcmToken = "fEvCxJFWSjWZyfsqiqMFJl:APA91bGr7IPRVsNnTgNTm9IE4UEUbIdGApDci77uTPYRrQpAfMGD6QyDeqQRf0aPHfenMYvd9dJOQwQiaHfmSDyO-05aqUOTPxxXSe1LSBy8f1cpdjVVE_ZfUPqrvFmyWBc8N5UqvT49";
        String fcmToken = getFCMTokenByUserId(dto.getReceiverUid());
        String SenderName = dto.getSenderName();
        // contentType 설정
        String msg = SenderName + "님이 좋아요를 누르셨습니다.";

        // overloading 함수 재사용
//        sendMessageTo(fcmToken, "Creaite", msg, https://i.imgur.com/WUtsRM9.png);
        sendMessageTo(fcmToken, "Creaite", msg, dto.getSenderImgSrc());
    }
    public String getFCMTokenByUserId(String userUid) {
        try {
            return USER_SERVER_CLIENT.get()
                    .uri("/fcm/" + userUid.toString())
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(Client4xxException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(Client5xxException::new))
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            return "Token 값 없음 ";
        }
    }

    private String makeMessage(String targetToken, String title, String body, String image) throws JsonParseException, JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(targetToken)
                        .notification(FcmMessage.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(image)
                                .build()
                        ).build()).validateOnly(false).build();

        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAccessToken() throws IOException {
        // firebase로 부터 access token을 가져온다.
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream())
                .createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();

        return googleCredentials.getAccessToken().getTokenValue();

    }
}