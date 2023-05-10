package D6B.D_discover_picture.picture.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PictureIsPublicRequest {
    public String uid;
    public Long pictureId;
}
