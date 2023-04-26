package D6B.D_discover_user.user.domain;

import lombok.*;

import javax.persistence.*;

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

    @Column(name = "is_loved")
    private Boolean isLoved;

    // 좋아요를 누른 유저 객체
    @ManyToOne
    @JoinColumn(name = "user_id")   // 매핑은 유저 id랑 해놓기
    private User user;

    // 좋아요가 눌러진 그림 -> MSA라 서비스가 따로 있어서 ManyToOne은 되지 않는다.
    @Column(name = "picture_id", nullable = false)
    private Long pictureId;
}
