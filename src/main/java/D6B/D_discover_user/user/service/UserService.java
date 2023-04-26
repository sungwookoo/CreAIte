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
     * 회원등록하기(첫번째 로그인 때 수행)
     */
    public Long enrollUser(User user, String token) {
        Optional<User> optUser = userRepository.findById(user.getId());
        // 이미 회원인 경우
        if(optUser.isPresent()) {
            return optUser.get().getId();   // 기존회원의 아이디를 반환한다.
        // 비회원이거나 이전에 탈퇴했던 유저인 경우
        } else {
            Optional<User> isUnActiveUser = userRepository.findByGId(user.getGId());
            // 비활성 유저인 경우
            if(isUnActiveUser.isPresent()) {
                User unActiveUser = isUnActiveUser.get();
                unActiveUser.setActivate(true);
                unActiveUser.setNickname(user.getNickname());
                unActiveUser.setImgSrc(user.getImgSrc());
                userRepository.save(unActiveUser);  // 비활성 유저를 다시 활성시킨다.
                return unActiveUser.getId();        // 회원의 id값을 반환한다.
            // 신규 유저인 경우
            } else {
                user.setId(null);   // autoincrement를 활용하기 위해서 null처리
                User newUser = userRepository.save(user);
                return newUser.getId();
            }
        }
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
