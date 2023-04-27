package D6B.D_discover_picture.picture.domain;

import D6B.D_discover_picture.common.StringToInstant;
import D6B.D_discover_picture.picture.controller.dto.PictureSaveRequest;
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

    @Column(name = "created_at")
    private Instant createdAt;

    public static Picture from(PictureSaveRequest pictureSaveRequest, Long userId) {
        System.out.println(pictureSaveRequest.getImg_url());
        return Picture.builder()
                .imgUrl(pictureSaveRequest.getImg_url())
                .makerId(userId)
                .isPublic(pictureSaveRequest.getIs_public())
                .loveCount(0L)
                .isDisabled(false)
                .isCreated(pictureSaveRequest.getIs_created())
                .createdAt(Instant.now().plusSeconds(60*60*9))
                .build();
    }
}
