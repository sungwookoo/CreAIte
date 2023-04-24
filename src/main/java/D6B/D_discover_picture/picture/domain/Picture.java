package D6B.D_discover_picture.picture.domain;

import lombok.*;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "picture")
public class Picture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "maker_id")
    private Long makerId;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "like_count")
    private Long likeCount;
}
