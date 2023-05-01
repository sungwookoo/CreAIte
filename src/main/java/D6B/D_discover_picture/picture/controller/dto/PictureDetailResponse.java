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
    public Long id;
    public String imgUrl;
    public String makerUid;
    public Long loveCount;
    public Boolean isCreated;
    public Instant createdAt;
    public List<String> imageTags;

    public static PictureDetailResponse from(Picture picture, List<String> tags) {
        return PictureDetailResponse.builder()
                .id(picture.getId())
                .imgUrl(picture.getImgUrl())
                .makerUid(picture.getMakerUid())
                .loveCount(picture.getLoveCount())
                .createdAt(picture.getCreatedAt())
                .imageTags(tags)
                .build();
    }
}
