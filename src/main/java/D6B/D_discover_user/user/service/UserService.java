package D6B.D_discover_user.user.service;

import D6B.D_discover_user.user.domain.Love;
import D6B.D_discover_user.user.domain.LoveRepository;
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
        // 좋아요가 있다면
        if(OptLove.isPresent()) {
            Love love = OptLove.get();
            // 좋아요가
            if(love.getIsLoved) {
                love.setIsLoved(false);
                loveRepository.save(love);
            }
            else {
                love.setIsLoved(true);
                loveRepository.save(love);
            }
        } else {

        }

        // 좋아요가 없다면
    }
}
