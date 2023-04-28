package D6B.D_discover_picture.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ConstValues {
    public static String USER_SERVER;
    public static WebClient USER_SERVER_CLIENT;

    @Value(value = "${userServer}")
    public void setUserServer(String userServer) {
        this.USER_SERVER = userServer;
        USER_SERVER_CLIENT = WebClient.builder().baseUrl(USER_SERVER).build();
    }
}
