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

    @Column(name = "love_count")
    private Long loveCount;

    @Column(name = "is_disabled")
    private Boolean isDisabled;

    // 이미지가 생성 or 편집인지를 나타냄
    @Column(name = "is_created")
    private Boolean isCreated;
}
