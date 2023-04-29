package D6B.D_discover_user.user.service.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteUserHistoryInPicture {
    private String uid;

    @JsonSerialize(using = ToStringSerializer.class)
    private List<Long> pictureIdxs;
    public DeleteUserHistoryInPicture(String uid, List<Long> pictureIdxs) {
        this.uid = uid;
        this.pictureIdxs = pictureIdxs;
    }
}
