package D6B.D_discover_user.user.service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class GetPictureUrlRequestDto {
    private List<Long> pictureIds;
    public GetPictureUrlRequestDto(List<Long> pictureIds) {
        this.pictureIds = pictureIds;
    }
}
