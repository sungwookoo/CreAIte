package D6B.D_discover_picture.picture.controller.dto;

import D6B.D_discover_picture.picture.domain.Picture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PictureDetailResponse {
    public Long pictureId;
    public String pictureUrl;
    public String makerUid;
    public Long loveCount;
    public Instant createdAt;
    public List<String> pictureTags;
    public Boolean loveCheck;
    public Boolean isPublic;

    public static PictureDetailResponse from(Picture picture, List<String> tags, Boolean isLoved) {
        return PictureDetailResponse.builder()
                .pictureId(picture.getId())
                .pictureUrl(picture.getImgUrl())
                .makerUid(picture.getMakerUid())
                .loveCount(picture.getLoveCount())
                .createdAt(picture.getCreatedAt())
                .pictureTags(tags)
                .loveCheck(isLoved)
                .isPublic(picture.getIsPublic())
                .build();
    }
}
