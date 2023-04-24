package D6B.D_discover_alarm.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;  // 좋아요를 누른사람
    private Long receiverId;    // 좋아요 받은 이미지를 만든사람
    private Long pictureId;     // 좋아요 받은 이미지 id
    private String content;     // 알람 내용
    private Instant createdAt;  // 알람 생성일
    private Boolean isSended;
    private Boolean isRead;
    private Integer type;

}
