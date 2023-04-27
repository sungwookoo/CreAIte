package D6B.D_discover_picture.picture.controller.dto;

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
    public String img_url;
    public Long maker_id;
    public Long love_count;
    public Boolean is_created;
    public Instant created_at;
    public List<String> image_tags;
}
