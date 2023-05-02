package D6B.D_discover_picture.picture.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PictureIdUrlResponse {
    public Long pictureId;
    public String imgUrl;
}
