package D6B.D_discover_alarm.controller.dto;

import D6B.D_discover_alarm.domain.Alarm;
import D6B.D_discover_alarm.service.AlarmService;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Instant;

@Data
@Builder
@ToString
public class AlarmDto {

    private Long id;
    public String senderUid;  // 좋아요를 누른사람
    public String receiverUid;    // 좋아요 받은 이미지를 만든사람
    public Long pictureId;     // 좋아요 받은 이미지 id
    public String content;     // 알람 내용
    public Instant createdAt;  // 알람 생성일
    public Boolean isRead;
    public Integer type;
    public String senderName;   // 보낸 사람 이름
    public String senderImgSrc; // 보낸사람 프로필 이미지
    public String pictureImgSrc; // 좋아요 사진 이미지

    public static AlarmDto from(Alarm alarm) {
        return AlarmDto.builder()
                .id(alarm.getId())
                .senderUid(alarm.getSenderUid())
                .receiverUid(alarm.getReceiverUid())
                .pictureId(alarm.getPictureId())
                .content(alarm.getContent())
                .createdAt(alarm.getCreatedAt())
                .isRead(alarm.getIsRead())
                .type(alarm.getType())
                .senderName(alarm.getSenderName())
                .senderImgSrc(alarm.getSenderImgSrc())
                .pictureImgSrc(alarm.getPictureImgSrc())
                .build();
    }
//    public static AlarmDto from(Alarm e, AlarmService alarmService) {
//        AlarmDto alarmDto = AlarmDto.from(e);
//        alarmDto.senderName = alarmService.getNameByUserId(e.getSenderId());
//        alarmDto.senderImgSrc = alarmService.getUserImgSrc(e.getSenderId());
//        alarmDto.pictureImgSrc = alarmService.getPictureImgSrc(e.getPictureId());
//        return alarmDto;
//    }

}
