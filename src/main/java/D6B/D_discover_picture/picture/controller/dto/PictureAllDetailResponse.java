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
@AllArgsConstructor
@NoArgsConstructor
public class PictureAllDetailResponse {
    public Long id;
    public String imgUrl;
    public String makerUid;
    public String makerName;
    public Long loveCount;
    public Instant createdAt;
    public List<String> imageTags;
    public Boolean loveCheck;

    public static PictureAllDetailResponse from(Picture picture, List<String> tags, Boolean isLoved, String makerName) {
        return PictureAllDetailResponse.builder()
                .id(picture.getId())
                .imgUrl(picture.getImgUrl())
                .makerUid(picture.getMakerUid())
                .makerName(makerName)
                .loveCount(picture.getLoveCount())
                .createdAt(picture.getCreatedAt())
                .imageTags(tags)
                .loveCheck(isLoved)
                .build();
    }
}
