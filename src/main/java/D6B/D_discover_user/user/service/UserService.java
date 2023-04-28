package D6B.D_discover_user.user.service;

import static D6B.D_discover_user.common.ConstValues.*;

import D6B.D_discover_user.user.controller.dto.LoveToggleRequestDto;
import D6B.D_discover_user.user.controller.dto.UserUpdateRequestDto;
import D6B.D_discover_user.user.domain.Love;
import D6B.D_discover_user.user.domain.LoveRepository;
import D6B.D_discover_user.user.domain.User;
import D6B.D_discover_user.user.domain.UserRepository;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.firebase.auth.FirebaseToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
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

    public String updateUserImg(MultipartFile image) {
        String accessKey = "AWS_ACCESS_KEY"; // AWS Access Key
        String secretKey = "AWS_SECRET_KEY"; // AWS Secret Key
        String region = "REGION_NAME"; // 지역 이름 (ex. ap-northeast-2)

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();

        String bucketName = "BUCKET_NAME"; // S3 버킷 이름
        String objectKey = "OBJECT_KEY"; // S3 객체 이름 (파일 이름)

        try {
            // 업로드할 파일을 InputStream으로 변환
            InputStream stream = image.getInputStream();

            // S3에 객체 업로드
            s3Client.putObject(bucketName, objectKey, stream, new ObjectMetadata());

            // 업로드한 객체의 URL 생성
            // 업로드한 객체의 URL을 반환하거나, 업로드한 객체를 참조하는 데이터베이스 등에 저장하는 등의 로직 수행
            return s3Client.getUrl(bucketName, objectKey).toExternalForm();
        } catch (IOException e) {
            // 예외 처리
            return null;
        }
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
            disableAlarm(uid);
        } else log.info("해당 uid에 대한 유저가 없습니다.");
    }

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

    // 날짜갱신하자
    public void toggleLove(LoveToggleRequestDto loveToggleRequestDto) {
        String uid = loveToggleRequestDto.getUid();
        Long pictureId = loveToggleRequestDto.getPicture_id();
        Long receiverId = loveToggleRequestDto.getMaker_id();
        Long senderId = findIdByUid(uid);
        String senderName = userRepository.findById(senderId).get().getName();
        String receiverName = userRepository.findById(receiverId).get().getName();
        Optional<Love> optLove = loveRepository.findByUserIdAndPictureId(senderId, pictureId);
        // 1. 기존의 좋아요 객체가 있는 경우
        if(optLove.isPresent()) {
            Love love = optLove.get();
            // 1-2. 좋아요가 원래있는 경우
            if(love.getIsActive()) { // 좋아요가 alive 상태라면 토글
            } else {    // 탈퇴하면서 지워진 좋아요
                // 1-3-2
            }
            loveRepository.save(love);
        // 2. 좋아요 생처음
        } else {
            // 2-1. 좋아요 만들어서 저장한다.
            loveRepository.save(Love.builder()
                            .isActive(true)
                            .user(findUserByUid(uid))
                            .pictureId(pictureId)
                            .build());
            // 2-2. 좋아요가 눌러진 사진의 url을 구해온다.
            String pictureUrl = getPictureUrlFromPictureIdAndPlus(senderId, pictureId);
            // 2-3. 해당 정보들을 알람서버에 보낸다.
            PostAlarm(senderId, receiverId, pictureId, senderName, receiverName, pictureUrl);
        }
    }

    public void disablePictureAndMinusLove(String uid, List<Long> pictureIdxs) {
        try {
            PICTURE_SERVER_CLIENT.get()
                    .uri("/picture/disableasdfasddsaasdafasdfsdadfsaas")// 여기 바뀔예정
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(void.class)
                    .block();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
    }

    public void minusLoveCount(Long pictureId) {
        try {
            PICTURE_SERVER_CLIENT.get()
                    .uri("/picture/minus_count")// 여기 바뀔예정
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(void.class)
                    .block();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
    }

    public void disableAlarm(String uid) {
        try {
            PICTURE_SERVER_CLIENT.get()
                    .uri("/picture/disable_alarm")// 여기 바뀔예정
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(void.class)
                    .block();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
    }

    // 좋아요 클릭시 좋아요 카운트 늘리게 한다.

    // 좋아요 취소시 갈 API
    public String getPictureUrlFromPictureIdAndMinus(Long userId, Long pictureId) {
        try {
            return PICTURE_SERVER_CLIENT.get()
                    .uri("/picture/asdfsadf")// 여기 바뀔예정
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
    public Void PostAlarm(Long senderId, Long receiverId, Long pictureId, String senderName, String receiverName, String pictureUrl) {
        try {
            return ALARM_SERVER_CLIENT.post()
                    .uri("/alarm/create")
                    .body(BodyInserters.fromValue())
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
