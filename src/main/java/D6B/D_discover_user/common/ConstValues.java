package D6B.D_discover_user.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component  // Component 스캔을 통해 자동으로 빈(bean)으로 등록할 클래스를 지정
public class ConstValues {
    public static String AUTH_SERVER;
    public static WebClient AUTH_SERVER_CLIENT;
    public static String ALARM_SERVER;
    public static WebClient ALARM_SERVER_CLIENT;
    public static String PICTURE_SERVER;
    public static WebClient PICTURE_SERVER_CLIENT;
    public static final String AUTH_URI = "";   // 구글인증에 따라서 달라짐
    public static final Long UNAUTHORIZED_USER = -2L;
    public static final Long NON_MEMBER = -1L;

    @Value(value = "${authServer}")
    public void setAuthServer(String authServer) {
        this.AUTH_SERVER = authServer;
        AUTH_SERVER_CLIENT = WebClient.builder().baseUrl(AUTH_SERVER).build();
    }

    @Value(value = "${alarmServer}")
    public void setAlarmServer(String alarmServer) {
        this.ALARM_SERVER = alarmServer;
        ALARM_SERVER_CLIENT = WebClient.builder().baseUrl(ALARM_SERVER).build();
    }

    @Value(value = "${pictureServer}")
    public void setPictureServer(String pictureServer) {
        this.PICTURE_SERVER = pictureServer;
        PICTURE_SERVER_CLIENT = WebClient.builder().baseUrl(PICTURE_SERVER).build();
    }

}
