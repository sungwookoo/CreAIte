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

    @Column(name = "sender_uid")
    private Long senderUid;  // 좋아요를 누른사람

    @Column(name = "sender_name")
    private String senderName;
    @Column(name = "sender_img_src")
    private String senderImgSrc;

    @Column(name = "receiver_uid")
    private Long receiverUid;    // 좋아요 받은 이미지를 만든사람


    @Column(name = "picture_uid")
    private Long pictureUid;     // 좋아요 받은 이미지 id

    @Column(name = "picture_img_src")
    private String pictureImgSrc;


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

    @Column(name = "is_alive")
    private Boolean isAlive;


//    public Alarm(Long receiverUid, Long senderUid, Long pictureUid) {
//        this.receiverUid = receiverUid;
//        this.senderUid = senderUid;
//        this.pictureUid = pictureUid;
//        //추가작업
//        this.isAlive = true;
//        // 필요한 경우 기본값으로 다른 필드 초기화
//        this.isSended = false;
//        this.isRead = false;
//        ZonedDateTime koreaTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
//        this.createdAt = koreaTime.toInstant();
//        this.type = 1;
//        this.content = "좋아요를 클릭하였습니다";
//    }
    public static Alarm createAlarm(NotificationDto dto) {
        return Alarm.builder()
                .receiverUid(dto.getReceiverUid())
                .senderUid(dto.getSenderUid())
                .pictureUid(dto.getPictureUid())
                .senderName(dto.getSenderName())
                .senderImgSrc(dto.getSenderImgSrc())
                .pictureImgSrc(dto.getPictureImgSrc())
                .isAlive(true)
                .isSended(false)
                .isRead(false)
                .createdAt(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant())
                .type(1)
                .content("좋아요를 클릭하였습니다")
                .build();
    }
}
