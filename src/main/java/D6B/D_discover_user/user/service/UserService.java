package D6B.D_discover_user.user.service;

import D6B.D_discover_user.user.controller.dto.UserRequestDto;
import D6B.D_discover_user.user.controller.dto.UserUpdateRequestDto;
import D6B.D_discover_user.user.domain.Love;
import D6B.D_discover_user.user.domain.LoveRepository;
import D6B.D_discover_user.user.domain.User;
import D6B.D_discover_user.user.domain.UserRepository;
import com.google.firebase.auth.FirebaseToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
//    private final LoveRepository loveRepository;
    private final UserRepository userRepository;

    public UserService(LoveRepository loveRepository,
                       UserRepository userRepository) {
//        this.loveRepository = loveRepository;
        this.userRepository = userRepository;
    }

    /**
     * 구글로그인으로 접속한 유저의 정보를 DB에 저장하거나 갱신시킨다.
     * @param firebaseToken : Firebase 로부터 받은 idToken -> 유저의 정보가 담겨있음
     * @return : 저장된 유저의 id
     */
    public Long enrollUser(FirebaseToken firebaseToken) {
        Optional<User> optUser = userRepository.findByUid(firebaseToken.getUid());
        // 이미 회원인 경우 -> 구글 정보를 바탕으로 닉네임정보, 이미지 정보를 갱신한다.
        if(optUser.isPresent()) {
            User activeUser = optUser.get();
            activeUser.setName(firebaseToken.getName());
            activeUser.setImgSrc(firebaseToken.getPicture());
            return optUser.get().getId();   // 기존회원의 아이디를 반환한다.
        // 신규유저이거나 이전에 탈퇴했던 유저인 경우
        } else {
            Optional<User> isUnActiveUser = userRepository.findByUid(firebaseToken.getUid());
            // 비활성 유저인 경우
            if(isUnActiveUser.isPresent()) {
                User unActiveUser = isUnActiveUser.get();
                unActiveUser.setActivate(true);
                unActiveUser.setName(firebaseToken.getName());
                unActiveUser.setImgSrc(firebaseToken.getPicture());
                unActiveUser.setCreatedAt(Instant.now().plusSeconds(60 * 60 * 9));
                userRepository.save(unActiveUser);  // 비활성 유저를 다시 활성시킨다.
                return unActiveUser.getId();        // 회원의 id값을 반환한다.
            // 신규 유저인 경우
            } else {
                User newUser = new User();
                newUser.setUid(firebaseToken.getUid());
                newUser.setEmail(firebaseToken.getEmail());
                newUser.setImgSrc(firebaseToken.getPicture());
                newUser.setCreatedAt(Instant.now().plusSeconds(60 * 60 * 9));
                newUser.setActivate(true);
                User user = userRepository.save(newUser);
                return user.getId();
            }
        }
    }

    /**
     * 유저 아이디로 유저 객체를 찾는 함수
     * @param userId : 찾고자 하는 유저의 id
     * @return : 해당 id를 가진 유저 객체
     */
    public User findUserByUserId(Long userId) {
        Optional<User> optUser = userRepository.findById(userId);
        return optUser.orElseThrow();
    }

//    /**
//     * 좋아요 토글 서비스 함수
//     * @param userId : 접속한 유저의 아이디
//     * @param pictureId : 클릭한 사진
//     */
//    public void clickLoveToggle(Long userId, Long pictureId) {
//        Optional<Love> optLove = loveRepository.findByUserIdAndPictureId(userId, pictureId);
//        // 좋아요 엔티티가 이미 있다면
//        if(optLove.isPresent()) {
//            Love love = optLove.get();
//            // 좋아요가 눌러진 상태라면
//            if(love.getIsLoved()) {
//                love.setIsLoved(false);
//                loveRepository.save(love);
//            }
//            // 좋아요가 눌리지지 않은 상태라면
//            else {
//                love.setIsLoved(true);
//                loveRepository.save(love);
//            }
//        // 좋아요 엔티티가 없다면 좋아요 엔티티를 생성해서 저장한다.
//        } else {
//            User user = userRepository.findById(userId).get();
//            loveRepository.save(Love.builder()
//                    .isLoved(Boolean.TRUE)
//                    .user(user)
//                    .pictureId(pictureId)
//                    .build());
//        }
//    }

//    /**
//     * 유저의 정보를 수정하는 것
//     * @param userId : 수정하고자 하는 유저의 아이디
//     * @param userUpdateRequestDto : 수정정보가 담긴 request dto
//     * @return : 수정된 유저의 객체
//     */
//    public User updateUserDetails(Long userId, UserUpdateRequestDto userUpdateRequestDto) {
//        Optional<User> optUser = userRepository.findById(userId);
//        // 유저가 있다면
//        if(optUser.isPresent()) {
//            User user = optUser.get();
//            user.setGender(userUpdateRequestDto.getGender());
//            user.setAge(userUpdateRequestDto.getAge());
//            user.setMobileNumber(userUpdateRequestDto.getMobileNumber());
//            return userRepository.save(user);
//        }
//        // ifPresent() -> Java 공부 더해야함
//        return optUser.get();
//    }
}
