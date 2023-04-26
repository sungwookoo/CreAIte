package D6B.D_discover_user.user.service;

import D6B.D_discover_user.user.domain.Love;
import D6B.D_discover_user.user.domain.LoveRepository;
import D6B.D_discover_user.user.domain.User;
import D6B.D_discover_user.user.domain.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final LoveRepository loveRepository;
    private final UserRepository userRepository;

    public UserService(LoveRepository loveRepository,
                       UserRepository userRepository) {
        this.loveRepository = loveRepository;
        this.userRepository = userRepository;
    }

    /**
     * 좋아요 토글 서비스 함수
     * @param userId : 접속한 유저의 아이디
     * @param pictureId : 클릭한 사진
     */
    public void clickLoveToggle(Long userId, Long pictureId) {
        Optional<Love> OptLove = loveRepository.findByUserIdAndPictureId(userId, pictureId);
        // 좋아요 엔티티가 이미 있다면
        if(OptLove.isPresent()) {
            Love love = OptLove.get();
            // 좋아요가 눌러진 상태라면
            if(love.getIsLoved()) {
                love.setIsLoved(false);
                loveRepository.save(love);
            }
            // 좋아요가 눌리지지 않은 상태라면
            else {
                love.setIsLoved(true);
                loveRepository.save(love);
            }
        // 좋아요 엔티티가 없다면 좋아요 엔티티를 생성해서 저장한다.
        } else {
            User user = userRepository.findById(userId).get();
            loveRepository.save(Love.builder()
                    .isLoved(Boolean.TRUE)
                    .user(user)
                    .pictureId(pictureId)
                    .build());
        }
    }
}
