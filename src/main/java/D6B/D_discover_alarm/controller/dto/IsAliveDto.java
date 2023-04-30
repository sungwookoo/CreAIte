package D6B.D_discover_alarm.controller.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.Instant;

@Data
@Builder
@ToString
public class IsAliveDto {

    public Long senderUid;      // 좋아요를 누른사람
    public Long receiverUid;    // 좋아요 받은 이미지를 만든사람
    public Long pictureId;     // 좋아요 받은 이미지 id

}
