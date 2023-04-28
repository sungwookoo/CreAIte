package D6B.D_discover_user.user.domain;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "love")
public class Love {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "user_uid")   // 매핑은 유저 id랑 해놓기
    private User user;

    @Column(name = "picture_id", nullable = false)
    private Long pictureId;
}
