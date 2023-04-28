package D6B.D_discover_alarm.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ConstValues {
    public static String USER_SERVER;
    public static WebClient USER_SERVER_CLIENT;
    public static String PICTURE_SERVER;
    public static WebClient PICTURE_SERVER_CLIENT;

    @Value(value = "${userServer}")
    public void setUserServer(String userServer) {
        this.USER_SERVER = userServer;
        USER_SERVER_CLIENT = WebClient.builder().baseUrl(USER_SERVER).build();
    }

    @Value(value = "${pictureServer}")
    public void setPictureServer(String pictureServer) {
        this.PICTURE_SERVER = pictureServer;
        PICTURE_SERVER_CLIENT = WebClient.builder().baseUrl(PICTURE_SERVER).build();
    }

}
