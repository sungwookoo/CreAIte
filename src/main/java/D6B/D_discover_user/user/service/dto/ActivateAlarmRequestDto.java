package D6B.D_discover_user.user.service.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ActivateAlarmRequestDto {
    private String senderUid;
    private String receiverUid;
    private Long pictureId;
    public ActivateAlarmRequestDto(String senderUid, String receiverUid, Long pictureId) {
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.pictureId = pictureId;
    }
}
