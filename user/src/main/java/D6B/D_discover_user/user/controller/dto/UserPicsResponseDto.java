package D6B.D_discover_user.user.controller.dto;

import D6B.D_discover_user.user.service.dto.UserMadeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPicsResponseDto {
    private Long pictureId;
    private String pictureUrl;
    private String makerUid;
    private Long loveCount;
    private Instant createdAt;
    private List<String> pictureTags;
    private Boolean loveCheck;
    private String makerName;
    private Boolean isPublic;

    /**
     * from 함수 : 받은 객체로부터 UserPicsResponseDto 만들기
     * @param responseFromPic : Picture Service 응답
     * @return : 빌드된 UserPicsResponseDto 하나를 반환한다.
     */
    public static UserPicsResponseDto from(UserMadeDto responseFromPic) {
        return UserPicsResponseDto.builder()
                .pictureId(responseFromPic.getPictureId())
                .pictureUrl(responseFromPic.getPictureUrl())
                .makerUid(responseFromPic.getMakerUid())
                .loveCount(responseFromPic.getLoveCount())
                .createdAt(responseFromPic.getCreatedAt())
                .pictureTags(responseFromPic.getPictureTags())
                .loveCheck(responseFromPic.getLoveCheck())
                .isPublic(responseFromPic.getIsPublic())
                .makerName(null)
                .build();
    }
}
