package D6B.D_discover_user.user.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
public class PostAlarmRequestDto {
    private String senderUid;
    private String receiverUid;
    private Long pictureId;
    private String senderImgSrc;
    private String senderName;
    private String pictureImgSrc;
    public PostAlarmRequestDto(String senderUid, String receiverUid, Long pictureId, String senderImgSrc, String senderName, String pictureImgSrc) {
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.pictureId = pictureId;
        this.senderImgSrc = senderImgSrc;
        this.senderName = senderName;
        this.pictureImgSrc = pictureImgSrc;
    }
}
