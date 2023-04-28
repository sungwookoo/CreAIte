package D6B.D_discover_alarm.controller.dto;

import D6B.D_discover_alarm.domain.Alarm;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationDto {
    private final String senderName;
    private final String senderImgSrc;
    private final String pictureImgSrc;
    private final Long senderUid;
    private final Long receiverUid;
    private final Long pictureUid;

    public static NotificationDto createNotificationDto(Alarm alarm){
        return  NotificationDto.builder()
                .senderUid(alarm.getSenderUid())
                .receiverUid(alarm.getReceiverUid())
                .pictureUid(alarm.getPictureUid())
                .senderName(alarm.getSenderName())
                .senderImgSrc(alarm.getSenderImgSrc())
                .pictureImgSrc(alarm.getPictureImgSrc())
                .build();
    }

}
