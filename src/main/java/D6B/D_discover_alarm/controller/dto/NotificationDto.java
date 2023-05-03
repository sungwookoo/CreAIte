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
    private final String senderUid;
    private final String receiverUid;
    private final Long pictureId;

    public static NotificationDto createNotificationDto(Alarm alarm){
        return  NotificationDto.builder()
                .senderUid(alarm.getSenderUid())
                .receiverUid(alarm.getReceiverUid())
                .pictureId(alarm.getPictureId())
                .senderName(alarm.getSenderName())
                .senderImgSrc(alarm.getSenderImgSrc())
                .pictureImgSrc(alarm.getPictureImgSrc())
                .build();
    }

}
