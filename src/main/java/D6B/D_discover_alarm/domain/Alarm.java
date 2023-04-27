package D6B.D_discover_alarm.domain;

import D6B.D_discover_alarm.controller.dto.NotificationDto;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "alarm")
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "sender_id")
    private Long senderId;  // 좋아요를 누른사람

//    @Column(name = "sender_name")
//    private Long senderName;  // 좋아요를 누른사람이름

    @Column(name = "receiver_id")
    private Long receiverId;    // 좋아요 받은 이미지를 만든사람

    @Column(name = "picture_id")
    private Long pictureId;     // 좋아요 받은 이미지 id

    @Column(name = "content")
    private String content;     // 알람 내용

    @Column(name = "created_at")
    private Instant createdAt;  // 알람 생성일

    @Column(name = "is_sended")
    private Boolean isSended;

    @Column(name = "is_read")
    private Boolean isRead;

    @Column(name = "type")
    private Integer type;

    public Alarm(Long receiverId, Long senderId, Long pictureId) {
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.pictureId = pictureId;
        // 필요한 경우 기본값으로 다른 필드 초기화
        this.isSended = false;
        this.isRead = false;
        ZonedDateTime koreaTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        this.createdAt = koreaTime.toInstant();
        this.type = 1;
        this.content = "좋아요를 클릭하였습니다";
    }
    public static Alarm createAlarm(NotificationDto dto) {
        return new Alarm(
                dto.getReceiverId(),
                dto.getSenderId(),
                dto.getPictureId()
        );
    }
}
