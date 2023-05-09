package D6B.D_discover_picture.picture.service.dto;

import D6B.D_discover_picture.picture.domain.Picture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PictureLoveCheckRequest {
    public String uid;
    public Long pictureId;
    public String makerUid;

    public static PictureLoveCheckRequest from (Picture picture, String uid) {
        return PictureLoveCheckRequest.builder()
                .uid(uid)
                .pictureId(picture.getId())
                .makerUid(picture.getMakerUid())
                .build();
    }
}
