package D6B.D_discover_user.user.service.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
public class DeactivateAlarmRequestDto {
    private String senderUid;
    private String receiverUid;
    private Long pictureId;

    public DeactivateAlarmRequestDto(String senderUid, String receiverUid, Long pictureId) {
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.pictureId = pictureId;
    }
}
