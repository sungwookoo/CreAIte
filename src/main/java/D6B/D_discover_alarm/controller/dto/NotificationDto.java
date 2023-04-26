package D6B.D_discover_alarm.controller.dto;

import D6B.D_discover_alarm.domain.Alarm;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationDto {
    private final Long senderId;
    private final Long receiverId;
    private final Long pictureId;

    public static NotificationDto createNotificationDto(Alarm alarm){
        return  NotificationDto.builder()
                .senderId(alarm.getId())
                .receiverId(alarm.getId())
                .pictureId()
    }

}
