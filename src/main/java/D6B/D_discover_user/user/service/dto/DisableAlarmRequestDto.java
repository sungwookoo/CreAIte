package D6B.D_discover_user.user.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
public class DisableAlarmRequestDto {
    private String senderUid;
    private String receiverUid;
    private Long pictureId;

    public DisableAlarmRequestDto(String senderUid, String receiverUid, Long pictureId) {
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.pictureId = pictureId;
    }
}
