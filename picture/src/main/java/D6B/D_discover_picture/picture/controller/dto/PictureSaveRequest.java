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
    public String uid;
    public String imgUrl;
    public Boolean isPublic;
    public Boolean isCreated;
    public List<String> imageTags;
    // 이미지에 달린 태그들
}
