package D6B.D_discover_user.user.service;

import static D6B.D_discover_user.common.ConstValues.*;

import D6B.D_discover_user.user.controller.dto.*;
import D6B.D_discover_user.user.domain.Love;
import D6B.D_discover_user.user.domain.LoveRepository;
import D6B.D_discover_user.user.domain.User;
import D6B.D_discover_user.user.domain.UserRepository;
import D6B.D_discover_user.user.service.dto.*;
import com.google.firebase.auth.FirebaseToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


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
        // 기존에 회원 정보가 있는 경우(회원 or 탈퇴했던 회원) -> 그냥 회원은 상관없음
        if(optUser.isPresent()) {
            User user = optUser.get();
            // 비활성 회원의 경우 다시 activate 해야한다.
            if(!user.getIsActive()) userRepository.save(activateUser(user, decodedToken));
        // 신규 회원
        } else {
            User user = new User(decodedToken);
            userRepository.save(user);
        }
    }

    // 회원정보를 초기화하고 활성화 시킨다.
    public User activateUser(User unActivateUser, FirebaseToken decodedToken) {
        unActivateUser.setIsActive(true);
        unActivateUser.setCreatedAt(Instant.now().plusSeconds(60 * 60 * 9));
        unActivateUser.setEmail(decodedToken.getEmail());
        unActivateUser.setName(decodedToken.getName());
        unActivateUser.setProfileImg(decodedToken.getPicture());
        unActivateUser.setGender(null);
        unActivateUser.setAge(null);
        return unActivateUser;
    }

    public User findUserByUid(String uid) {
        Optional<User> optUser = userRepository.findByUid(uid);
        if(optUser.isPresent()) return optUser.get();
        else log.info("해당 uid에 대한 유저가 없습니다");
        return null;
    }

    public void updateUserImg(UserImgUpdateRequestDto userImgUpdateRequestDto) {
        String uid = userImgUpdateRequestDto.getUid();
        Optional<User> optUser = userRepository.findByUid(uid);
        if(optUser.isPresent()) {
            User user = optUser.get();
            user.setProfileImg(userImgUpdateRequestDto.getProfileImg());
            userRepository.save(user);
        } else log.info("해당 uid에 대한 유저가 없습니다.");
    }

    public User updateUserInfos(FirebaseToken decodedToken, UserUpdateRequestDto userUpdateRequestDto) {
        Optional<User> optUser = userRepository.findByUid(decodedToken.getUid());
        if(optUser.isPresent()) {
            User user = optUser.get();
            return userRepository.save(updateUserWithoutImg(user, userUpdateRequestDto));
        } else log.info("해당 uid에 대한 유저가 없습니다.");
        return null;
    }

    public User updateUserWithoutImg(User user, UserUpdateRequestDto userUpdateRequestDto) {
        user.setName(userUpdateRequestDto.getName());
        user.setGender(userUpdateRequestDto.getGender());
        user.setAge(userUpdateRequestDto.getAge());
        return user;
    }

    // 삭제
    public void deleteUserInfo(FirebaseToken decodedToken) {
        String uid = decodedToken.getUid();
        Optional<User> optUser = userRepository.findByUid(uid);
        if(optUser.isPresent()) {
            User user = optUser.get();
            user.setIsActive(false);
            userRepository.save(user);
            // 좋아요 비활성화 시키기 및 해당 좋아요로부터 그림의 id 뽑아내기
            List<Long> madeOrLoved = disableLove(uid);
            // 해당 유저가 그림 비활성화 및 좋아요 누른 그림의 좋아요 수 줄이기
            disablePictureAndMinusLove(uid, madeOrLoved);
            // 알람 비활성화 시키기 - 주는자, 받는자
            disableAlarms(uid);
        } else log.info("해당 uid에 대한 유저가 없습니다.");
    }

    /**
     * uid 통해서 id를 찾는 함수(확장성 대비용)
     * @param uid : 사용자의 uid
     * @return : 사용자의 id
     */
    public Long findIdByUid(String uid) {
        User user = findUserByUid(uid);
        return user.getId();
    }

    // 비활성 유저의 좋아요는 비활성화 시키는 함수
    public List<Long> disableLove(String uid) {
        List<Love> loves = loveRepository.findByUserId(findIdByUid(uid));
        List<Long> loveIdxs = new ArrayList<>();
        for(Love love : loves) {
            love.setIsActive(false);
            loveIdxs.add(love.getPictureId());
            loveRepository.save(love);
        }
        return loveIdxs;
    }

    public void toggleLove(LoveToggleRequestDto loveToggleRequestDto) {
        String uid = loveToggleRequestDto.getUid();
        Long pictureId = loveToggleRequestDto.getPictureId();
        String receiverUid = loveToggleRequestDto.getMakerUid();
        String senderUid = loveToggleRequestDto.getUid();
        String senderName = userRepository.findByUid(senderUid).get().getName();
        String receiverName = userRepository.findByUid(receiverUid).get().getName();
        Optional<Love> optLove = loveRepository.findByUserUidAndPictureId(senderUid, pictureId);
        // 1. 기존의 좋아요 객체가 있는 경우
        if(optLove.isPresent()) {
            Love love = optLove.get();
            // 1-2. 좋아요 취소 -> 알람도 비활성화, 그림의 카운트를 하나 내려야한다.
            if(love.getIsActive()) {
                love.setIsActive(false);    // 좋아요 취소
                disableAlarm(senderUid, receiverUid, pictureId);    // 알람 비활성화
                minusLoveCount(pictureId);  // 그림의 카운트 하나 내리기
                loveRepository.save(love);
            // 1-3. 다시 좋아요 활성화 -> 알람 활성화, 그림의 카운트를 하나 올림
            } else {
                love.setIsActive(true);
                activateAlarm(senderUid, receiverUid, pictureId);   // 알람 활성화
                plusLoveCount(pictureId);   // 좋아요 하나 추가
                loveRepository.save(love);
            }
        // 2. 좋아요 쌩처음
        } else {
            // 2-1. 좋아요 만들어서 저장한다.
            loveRepository.save(Love.builder()
                            .isActive(true)
                            .user(findUserByUid(uid))
                            .pictureId(pictureId)
                            .build());
            // 2-2. 좋아요가 눌러진 사진의 url 구해온다.
            String pictureUrl = getPictureUrlAndPlusLove(pictureId);
            // 2-3. 해당 정보들을 알람서버에 보내 알림을 생성
            PostAlarm(senderUid, receiverUid, pictureId, senderName, receiverName, pictureUrl);
        }
    }

    public void deActiveLove(Long pictureId) {
        List<Love> loves = loveRepository.findByPictureId(pictureId);
        for(Love love : loves) {
            love.setIsActive(false);
            loveRepository.save(love);
        }
    }

    public List<LoveCheckAndMakerResponseDto> findLoveChecksAndMakers(List<LoveCheckAndMakerRequestDto> loveCheckAndMakerRequestDtos) {
        List<LoveCheckAndMakerResponseDto> responseDtos = new ArrayList<>();
        for(LoveCheckAndMakerRequestDto loveCheckAndMakerRequestDto : loveCheckAndMakerRequestDtos) {
            String uid = loveCheckAndMakerRequestDto.getUid();
            String makerUid = loveCheckAndMakerRequestDto.getMakerUid();
            Long pictureId = loveCheckAndMakerRequestDto.getPictureId();
            Optional<User> optMaker = userRepository.findByUid(makerUid);
            optMaker.ifPresent(user -> responseDtos.add(LoveCheckAndMakerResponseDto.builder()
                    .loveCheck(loveRepository.findByUserUidAndPictureIdAndIsActiveTrue(uid, pictureId).isPresent())
                    .makerName(user.getName())
                    .build()));
        }
        return responseDtos;
    }

    public List<String> findMakers(List<String> makerUids) {
        List<String> responseDtos = new ArrayList<>();
        for(String makerUid : makerUids) {
            Optional<User> optUser = userRepository.findByUid(makerUid);
            optUser.ifPresent(user -> responseDtos.add(user.getName()));
        }
        return responseDtos;
    }

    /**
     * 유저 탈퇴(비활성화) 시, 값을 해당 유저의 그림을 비활성화하고 좋아요 수를 하나 줄인다.
     * @param uid : 탈퇴한 유저의 uid
     * @param pictureIdxs : 유저가 좋아요 눌렀던 그림 idx
     */
    public void disablePictureAndMinusLove(String uid, List<Long> pictureIdxs) {
        try {
            PICTURE_SERVER_CLIENT.post()
                    .uri("/picture/delete/user")// 여기 바뀔예정
                    .body(BodyInserters.fromValue(new DeleteUserHistoryInPicture(uid, pictureIdxs)))
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(void.class)
                    .block();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
    }

    /**
     * 좋아요 취소 시, 해당 그림의 좋아요 수를 하나 줄임
     * @param pictureId : 그림의 id
     */
    public void minusLoveCount(Long pictureId) {
        try {
            PICTURE_SERVER_CLIENT.post()
                    .uri("/picture/delete/count/" + pictureId)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(void.class)
                    .block();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
    }

    /**
     * 좋아요 활성 시, 해당 그림의 좋아요 수를 하나 올림
     * @param pictureId : 그림의 id
     */
    public void plusLoveCount(Long pictureId) {
        try {
            PICTURE_SERVER_CLIENT.get()
                    .uri("/picture/create/count/" + pictureId)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(void.class)
                    .block();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
    }

    /**
     * 유저 탈퇴 시, 유저의 모든 알람을 비활성화
     * @param uid : 탈퇴한 유저의 uid
     */
    public void disableAlarms(String uid) {
        try {
            PICTURE_SERVER_CLIENT.put()
                    .uri("/alarm/remove/" + uid)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(void.class)
                    .block();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
    }

    /**
     * 좋아요 '처음' 누르면 알람을 그림 url 획득 및 해당 그림에 좋아요 추가 요청
     * @param pictureId : 그림의 url
     */
    public String getPictureUrlAndPlusLove(Long pictureId) {
        try {
            return PICTURE_SERVER_CLIENT.post()
                    .uri("/picture/create/count/" + pictureId)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
        return null;
    }

    /**
     * 좋아요 '처음' 누르면, 알람 추가 요청
     * @param senderUid : 좋아요 누른 사람의 uid
     * @param receiverUid : 좋아요 받은 그림의 주인 uid
     * @param pictureId : 그림의 id
     * @param senderName : 좋아요 누른 사람의 이름
     * @param receiverName : 좋아요 받은 그림의 주인 이름
     * @param pictureUrl : 그림의 url
     */
    public void PostAlarm(String senderUid, String receiverUid, Long pictureId, String senderName, String receiverName, String pictureUrl) {
        try {
            ALARM_SERVER_CLIENT.post()
                    .uri("/alarm/create")
                    .body(BodyInserters.fromValue(new PostAlarmRequestDto(senderUid, receiverUid, pictureId, senderName, receiverName, pictureUrl)))
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(void.class)
                    .block();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
    }

    /**
     * 좋아요를 '다시'누르면, 알람을 활성화 시킨다.
     * @param senderUid : 좋아요 누른 사람의 uid
     * @param receiverUid : 좋아요 받은 그림의 주인 uid
     * @param pictureId : 그림의 id
     */
    public void activateAlarm(String senderUid, String receiverUid, Long pictureId) {
        try {
            ALARM_SERVER_CLIENT.put()
                    .uri("/alarm/marked")
                    .body(BodyInserters.fromValue(new ActivateAlarmRequestDto(senderUid, receiverUid, pictureId)))
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(void.class)
                    .block();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
    }

    /**
     * 좋아요 취소시, 알람을 비활성화
     * @param senderUid : 좋아요 누른 사람의 uid
     * @param receiverUid : 좋아요 받은 그림의 주인 uid
     * @param pictureId : 그림의 id
     */
    public void disableAlarm(String senderUid, String receiverUid, Long pictureId) {
        try {
            ALARM_SERVER_CLIENT.put()
                    .uri("/alarm/isalive")
                    .body(BodyInserters.fromValue(new DisableAlarmRequestDto(senderUid, receiverUid, pictureId)))
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(void.class)
                    .block();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
    }

    /**
     * 본인이 누른 좋아요 리스트를 찾는 함수
     * @param decodedToken : 로그인 유저의 토큰값
     * @return : 좋아요 리스트에 올라갈 그림의 디테일
     */
    public List<UserPicsResponseDto> findMyLovePics(FirebaseToken decodedToken) {
        List<Long> pictureIds = loveRepository.findByUserUidAndIsActiveTrueOrderByCreatedAtDesc(decodedToken.getUid())
                .stream()
                .map(Love::getPictureId)
                .collect(Collectors.toList());
        try {
            return PICTURE_SERVER_CLIENT.post()
                    .uri("/picture/like_all_list")
                    .body(BodyInserters.fromValue(pictureIds))
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(new ParameterizedTypeReference<List<UserPicsResponseDto>>() {})
                    .block();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
        return null;
    }


    /**
     * 다른 유저의 좋아요 리스트를 찾는 함수(로그인 유저)
     * @param decodedToken : 로그인 유저의 토큰값
     * @param uid : 찾고있는 대상의 uid
     * @return : 좋아요 리스트에 올라갈 그림의 디테일
     */
    public List<UserPicsResponseDto> findUserLovePicsCertified(FirebaseToken decodedToken, String uid) {
        // 관심 대상 uid를 통해서 그 사람이 좋아한 그림들을 찾는다.(id)
        List<Long> pictureIds = loveRepository.findByUserUidAndIsActiveTrueOrderByCreatedAtDesc(uid)
                .stream()
                .map(Love::getPictureId)
                .collect(Collectors.toList());
        // 해당 그림에 대한 정보를 모두 가져온다.
        try {
            List<UserPicsResponseDto> responseDtos = PICTURE_SERVER_CLIENT.post()
                    .uri("/picture/like_public_list")
                    .body(BodyInserters.fromValue(pictureIds))
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(new ParameterizedTypeReference<List<UserPicsResponseDto>>() {})
                    .block();
            String loginUid = decodedToken.getUid();
            // 해당 그림을 지금 접속한 사람도 좋아요를 눌렀는지 여부를 판단한다.
            // responseDtos를 순회하면서 pictrueId와 decodedToken으로 좋아요가 있고, 있다면 활성화되어 있는지를 찾는다.
            for(UserPicsResponseDto responseDto : Objects.requireNonNull(responseDtos)) {
                Long pictureId = responseDto.getPictureId();
                Optional<Love> optLove = loveRepository.findByUserUidAndPictureIdAndIsActiveTrue(loginUid, pictureId);
                if(optLove.isPresent()) {
                    responseDto.setLoveCheck(Boolean.TRUE);
                }
            }
            return responseDtos;
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
        return null;
    }

    /**
     * 비로그인 상태로 특정 uid 사람의 좋아요 리스트를 찾는 함수
     * @param uid : 관심 대상의 uid
     * @return : 좋아요 리스트에 올라갈 그림의 디테일
     */
    public List<UserPicsResponseDto> findUserLovePics(String uid) {
        // 관심 대상 uid를 통해서 그 사람이 좋아한 그림들을 찾는다.(id)
        List<Long> pictureIds = loveRepository.findByUserUidAndIsActiveTrueOrderByCreatedAtDesc(uid)
                .stream()
                .map(Love::getPictureId)
                .collect(Collectors.toList());
        // 해당 그림에 대한 정보를 모두 가져온다.
        try {
            List<UserPicsResponseDto> responseDtos = PICTURE_SERVER_CLIENT.post()
                    .uri("/picture/like_public_list")
                    .body(BodyInserters.fromValue(pictureIds))
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(new ParameterizedTypeReference<List<UserPicsResponseDto>>() {})
                    .block();
            return responseDtos;
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
        return null;
    }

    /**
     * 유저가 만든 그림 or 사진
     * @param uid : 좋아요 누른 사람의 uid
     * @return : 유저가 만든 그림의 id, url, createdAt 리스트
     */
    public List<UserPicsResponseDto> findUserMadePics(String uid) {
        try {
            return PICTURE_SERVER_CLIENT.get()
                    //*****************여기 수정하기*******************//
                    .uri("/picture/asdfasasdfsdfafdssddsd/" + uid)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(new ParameterizedTypeReference<List<UserPicsResponseDto>>() {})
                    .block();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
        return null;
    }

//    public void MsaCallPicture(String url, ) {
//        try {
//            PICTURE_SERVER_CLIENT.post()
//                    .uri("/picture/delete/user")// 여기 바뀔예정
//                    .body(BodyInserters.fromValue(new DeleteUserHistoryInPicture(uid, pictureIdxs)))
//                    .retrieve()
//                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
//                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
//                    .bodyToMono(void.class)
//                    .block();
//        } catch (Exception e) {
//            log.error("{}", e.getMessage());
//        }
//    }

}
