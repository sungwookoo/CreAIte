package D6B.D_discover_alarm.domain;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

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

}
