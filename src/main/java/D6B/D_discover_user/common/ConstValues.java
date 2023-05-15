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

    public static String USER_HISTORY_DELETE_TO_PICTURE = "/delete/user";
    public static String COUNT_DOWN_LOVE_TO_PICTURE = "/delete/count/";
    public static String COUNT_UP_LOVE_TO_PICTURE = "/create/count/";
    public static String USER_MADE_TO_PICTURE_WHEN_LOGIN = "/made/user/";
    public static String USER_MADE_TO_PICTURE_WHEN_NOT_LOGIN = "/made/no_user/";
    public static String USER_LIKES_TO_PICTURE_WHEN_NOT_ME = "/like_public_list";
    public static String USER_LIKES_TO_PICTURE_WHEN_ME = "/like_all_list";
    public static String CREATE_ALARM_TO_ALARM = "/create";
    public static String MARK_ALARM_TO_ALARM = "/marked";
    public static String CANCEL_ALARM_TO_ALARM = "/isalive";
    public static String USER_HISTORY_DELETE_TO_ALARM = "/remove/";

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
