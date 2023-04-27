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
import java.util.List;
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

    public void enrollUser(FirebaseToken decodedToken) {
        Optional<User> optUser = userRepository.findByUid(decodedToken.getUid());
        if(optUser.isPresent()) {
            User user = optUser.get();
            if(user.getActivate()) {
                user.setName(decodedToken.getName());
                user.setImgSrc(decodedToken.getPicture());
                userRepository.save(user);
            } else {    // 회원탈퇴 상태였던 경우
                userRepository.save(activateUser(user, decodedToken));
            }
        // 신규 회원
        } else {
            User user = new User(decodedToken);
            userRepository.save(user);
        }
    }

    public User activateUser(User unActivateUser, FirebaseToken decodedToken) {
        unActivateUser.setActivate(true);
        unActivateUser.setName(decodedToken.getName());
        unActivateUser.setImgSrc((decodedToken.getPicture()));
        unActivateUser.setCreatedAt(Instant.now().plusSeconds(60 * 60 * 9));
        return unActivateUser;
    }

    public User findUserByUid(String uid) {
        Optional<User> optUser = userRepository.findByUid(uid);
        if(optUser.isPresent()) return optUser.get();
        else log.info("해당 uid에 대한 유저가 없습니다");
        return null;
    }

    public User updateUserInfos(FirebaseToken decodedToken, UserUpdateRequestDto userUpdateRequestDto) {
        Optional<User> optUser = userRepository.findByUid(decodedToken.getUid());
        if(optUser.isPresent()) {
            User user = optUser.get();
            user.setAge(userUpdateRequestDto.getAge());
            user.setGender(userUpdateRequestDto.getGender());
            user.setMobileNumber(userUpdateRequestDto.getMobile_number());
            return userRepository.save(user);
        } else log.info("해당 uid에 대한 유저가 없습니다.");
        return null;
    }

    public void deleteUserInfo(FirebaseToken decodedToken) {
        Optional<User> optUser = userRepository.findByUid(decodedToken.getUid());
        if(optUser.isPresent()) {
            User user = optUser.get();
            user.setActivate(false);
            userRepository.save(user);
            disableLove(user.getUid());
        } else log.info("해당 uid에 대한 유저가 없습니다.");
    }

    public Long findIdByUid(String uid) {
        User user = findUserByUid(uid);
        return user.getId();
    }

    // 탈퇴한 유저의 좋아요는 비활성화 시키는 함수
    public void disableLove(String uid) {
        List<Love> loves = loveRepository.findByUserId(findIdByUid(uid));
        for(Love love : loves) {
            love.setIsLoved(false);
            love.setIsAlive(false);
            loveRepository.save(love);
        }
    }

    public void toggleLove(String uid, Long pictureId) {
        Long userId = findIdByUid(uid);
        Optional<Love> optLove = loveRepository.findByUserIdAndPictureId(userId, pictureId);
        // 기존의 좋아요 객체가 있는 경우
        if(optLove.isPresent()) {
            Love love = optLove.get();
            if(love.getIsAlive()) { // 좋아요가 alive 상태라면 토글
                love.setIsLoved(!love.getIsLoved());
            } else {    // 탈퇴하면서 지워진 좋아요
                love.setIsAlive(true);
                love.setIsLoved(true);
            }
            loveRepository.save(love);
        // 좋아요 객체를 처음 생성해야하는 경우
        } else {
            loveRepository.save(Love.builder()
                            .isLoved(true)
                            .isAlive(true)
                            .user(findUserByUid(uid))
                            .pictureId(pictureId)
                            .build());
        }
    }

}
