package D6B.D_discover_user.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ConstValues {
    public static String ALARM_SERVER;
    public static WebClient ALARM_SERVER_CLIENT;
    public static String PICTURE_SERVER;
    public static WebClient PICTURE_SERVER_CLIENT;

    @Value(value = "${pictureServer}")
    public void setPictureServer(String pictureServer) {
        this.PICTURE_SERVER = pictureServer;
        PICTURE_SERVER_CLIENT = WebClient.builder().baseUrl(PICTURE_SERVER).build();
    }

    @Value(value = "${alarmServer}")
    public void setAlarmServer(String alarmServer) {
        this.ALARM_SERVER = alarmServer;
        ALARM_SERVER_CLIENT = WebClient.builder().baseUrl(ALARM_SERVER).build();
    }
}
