package D6B.D_discover_user.user.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
public class GetPictureUrlRequestDto {
    private List<Long> pictureIds;
    public GetPictureUrlRequestDto(List<Long> pictureIds) {
        this.pictureIds = pictureIds ;
    }
}
