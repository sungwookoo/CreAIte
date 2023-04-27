package D6B.D_discover_user.user.service;

import static D6B.D_discover_user.common.ConstValues.*;

import D6B.D_discover_user.user.controller.dto.UserUpdateRequestDto;
import D6B.D_discover_user.user.domain.Love;
import D6B.D_discover_user.user.domain.LoveRepository;
import D6B.D_discover_user.user.domain.User;
import D6B.D_discover_user.user.domain.UserRepository;
import com.google.firebase.auth.FirebaseToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
        // 기존에 회원 정보가 있는 경우(회원 or 탈퇴했던 회원)
        if(optUser.isPresent()) {
            User user = optUser.get();
            // 탈퇴한 회원의 경우 다시 activate 해야한다.
            if(!user.getActivate()) userRepository.save(activateUser(user, decodedToken));
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
            return userRepository.save(updateUser(user, userUpdateRequestDto));
        } else log.info("해당 uid에 대한 유저가 없습니다.");
        return null;
    }

    public User updateUser(User user, UserUpdateRequestDto userUpdateRequestDto) {
        user.setAge(userUpdateRequestDto.getAge());
        user.setGender(userUpdateRequestDto.getGender());
        user.setMobileNumber(userUpdateRequestDto.getMobile_number());
        user.setImgSrc(userUpdateRequestDto.getImg_src());
        user.setName(userUpdateRequestDto.getName());
        return user;
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

    // msa 적용할 함수를 만들어보자
    // 1. 먼저 picture에 가서 count수를 조정하고 picture url을 받아옴
    // 2. picture_url을 던져주면서 알람을 하나 생성하고 알람을 보내는 함수를 수행해야함
    public String getPictureUrlFromPictureIdAndPlus(Long pictureId) {
        try {
            return PICTURE_SERVER_CLIENT.get()
                    .uri("/picture/")// 여기 바뀔예정
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
            return "Can't get picture url";
        }
    }

    // 고민내용 : 좋아요 숫자를 따로 반정규화해놓는게 맞을까? 그렇다면, 이걸 딱 맞게 관리하는 방법이 중요할듯하다.
    // 실제 좋아요를 삭제하는 식으로 만드는 게 맞을까?
    // 좋아요를 삭제할지, 아니면 비활성화할지에 대해서 얘기가 확실하게 필요하다
    // 지금은 좋아요를 삭제하진 않는다.

    // 좋아요 취소 시, 마이너스
    public String getPictureUrlFromPictureIdAndMinus(Long pictureId) {
        try {
            return PICTURE_SERVER_CLIENT.get()
                    .uri("/picture/")// 여기 바뀔예정
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
            return "Can't get picture url";
        }
    }

    // 좋아요 누르면, 알람을 추가한다.
    public Void PostAlarm(Long senderId, Long receiverId, Long pictureId, String senderName, String receiverName, Long pictureName) {
        try {
            return ALARM_SERVER_CLIENT.get()
                    .uri("/alarm/")// 여기 바뀔예정
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(void.class)
                    .block();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
        return null;
    }

    // 좋아요 취소시, 알람을 지운다.
    public Void DeleteAlarm(Long senderId, Long receiverId, Long pictureId) {
        try {
            return ALARM_SERVER_CLIENT.get()
                    .uri("/alarm/")// 여기 바뀔예정
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(void.class)
                    .block();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
        return null;
    }
}
