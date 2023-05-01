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
public class DeleteUserRequest {
    public String uid;
    public List<Long> pictureIdxs;
}
