package D6B.D_discover_alarm.controller.dto;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
@Getter
@ToString
public class RequestDTO {
    private String targetToken;
    private String title;
    private String body;
    private String image;

}
