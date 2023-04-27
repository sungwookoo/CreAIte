package D6B.D_discover_picture.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ConstValues {
//    public static String AUTH_SERVER;
//    public static WebClient AUTH_SERVER_CLIENT;
//    public static String USER_SERVER;
//    public static WebClient USER_SERVER_CLIENT;
    public static final String AUTH_URI = "";  // firebase 인증
    public static final Long UNAUTHORIZED_USER = -2L;
    public static final Long NON_MEMBER = -1L;

//    @Value(value = "${userServer}")
//    public void setUserServer(String userServer) {
//        this.USER_SERVER = userServer;
//        USER_SERVER_CLIENT = WebClient.builder().baseUrl(USER_SERVER).build();
//    }
//
//    @Value(value = "${authServer}")
//    public void setAuthServer(String authServer) {
//        this.AUTH_SERVER = authServer;
//        AUTH_SERVER_CLIENT = WebClient.builder().baseUrl(AUTH_SERVER).build();
//    }
}
