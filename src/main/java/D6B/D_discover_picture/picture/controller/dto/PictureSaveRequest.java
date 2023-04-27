package D6B.D_discover_picture.picture.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PictureSaveRequest {
    public String img_url;
    public Boolean is_public;
    public Boolean is_created;
    public List<String> tags;
    // 이미지에 달린 태그들
}
