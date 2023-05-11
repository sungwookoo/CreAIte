package D6B.D_discover_picture.picture.domain;

import D6B.D_discover_picture.picture.controller.dto.PictureSaveRequest;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

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

    @Column(name = "picture_url")
    private String imgUrl;

    @Column(name = "maker_uid")
    private String makerUid;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "love_count")
    private Long loveCount;

    @Column(name = "is_alive")
    private Boolean isAlive;

    // 이미지가 생성 or 편집인지를 나타냄
    @Column(name = "is_created")
    private Boolean isCreated;

    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(mappedBy = "picture")
    @ToString.Exclude
    @Builder.Default
    private Set<PictureTag> pictureTags = new LinkedHashSet<>();

    public static Picture from(PictureSaveRequest pictureSaveRequest) {
        return Picture.builder()
                .imgUrl(pictureSaveRequest.getImgUrl())
                .makerUid(pictureSaveRequest.getUid())
                .isPublic(pictureSaveRequest.getIsPublic())
                .loveCount(0L)
                .isAlive(Boolean.TRUE)
                .isCreated(pictureSaveRequest.getIsCreated())
                .createdAt(Instant.now().plusSeconds(60*60*9))
                .build();
    }
}
