package D6B.D_discover_picture.picture.service;

import D6B.D_discover_picture.picture.controller.dto.DeleteUserRequest;
import D6B.D_discover_picture.picture.controller.dto.PictureAllDetailResponse;
import D6B.D_discover_picture.picture.controller.dto.PictureDetailResponse;
import D6B.D_discover_picture.picture.controller.dto.PictureSaveRequest;
import D6B.D_discover_picture.picture.domain.*;
import D6B.D_discover_picture.picture.service.dto.LoveCheckAndMakerResponse;
import D6B.D_discover_picture.picture.service.dto.PictureLoveCheckRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;

import static D6B.D_discover_picture.common.ConstValues.*;

@Slf4j
@Service
public class PictureService {
    private final PictureRepository pictureRepository;
    private final PictureTagRepository pictureTagRepository;
    private final TagRepository tagRepository;
    private final WeeklyTopPictureRepository weeklyTopPictureRepository;
    private final MonthlyTopPictureRepository monthlyTopPictureRepository;

    @Autowired
    public PictureService(PictureRepository pictureRepository,
                          PictureTagRepository pictureTagRepository,
                          TagRepository tagRepository,
                          WeeklyTopPictureRepository weeklyTopPictureRepository,
                          MonthlyTopPictureRepository monthlyTopPictureRepository) {
        this.pictureRepository = pictureRepository;
        this.pictureTagRepository = pictureTagRepository;
        this.tagRepository = tagRepository;
        this.weeklyTopPictureRepository = weeklyTopPictureRepository;
        this.monthlyTopPictureRepository = monthlyTopPictureRepository;
    }

    public void savePicture(PictureSaveRequest pictureSaveRequest) {
        // 이미지 저장
        Picture picture = Picture.from(pictureSaveRequest);
        Picture newPicture = pictureRepository.save(picture);
        // 이미지에 달린 태그들.
        List<String> image_tags = pictureSaveRequest.getImageTags();
        for (String tag: image_tags) {
            Optional<Tag> opTag = tagRepository.findByWord(tag);
            Tag itag;
            if (opTag.isPresent()) {
                // 태그가 이미 존재한다면 그냥 들고오기
                itag = opTag.get();
            } else {
                // 없으면 새로 저장하고 가져오기
                Tag ntag = new Tag();
                ntag.setWord(tag);
                itag = tagRepository.save(ntag);
            }
            pictureTagRepository.save(PictureTag
                    .builder()
                    .tag(itag)
                    .picture(newPicture)
                    .build()
            );
        }
    }

    public void deletePicture(Long pictureId, String uid) {
        Optional<Picture> opPicture = pictureRepository.findById(pictureId);
        if (opPicture.isPresent()) {
            Picture picture = opPicture.get();
            if (picture.getMakerUid().equals(uid)) {
                picture.setIsAlive(Boolean.FALSE);
                pictureRepository.save(picture);
                // 해당 이미지의 좋아요 삭제 요청, 알림 삭제 요청 보내야함
                deleteLikeRequest(pictureId);
                deleteLikeAlarmRequest(pictureId);
                return;
            } else {
                throw new IllegalStateException("이미지를 올린 사람이 아닙니다.");
            }
        }
        throw new IllegalStateException("없는 이미지 입니다.");
    }

    // loveCount 올리기
    public String plusCount(Long pictureId) {
        Optional<Picture> opPicture = pictureRepository.findById(pictureId);
        if (opPicture.isPresent()) {
            Picture picture = opPicture.get();
            if (picture.getIsAlive() == Boolean.TRUE) {
                picture.setLoveCount(picture.getLoveCount() + 1);
                pictureRepository.save(picture);
                return picture.getImgUrl();
            } else {
                throw new IllegalStateException("삭제된 이미지입니다.");
            }
        }
        throw new IllegalStateException("해당 이미지가 없습니다.");
    }

    // 좋아요 취소 시 count 낮추기
    public void minusCount(Long pictureId) {
        Optional<Picture> opPicture = pictureRepository.findById(pictureId);
        if (opPicture.isPresent()) {
            Picture picture = opPicture.get();
            if (picture.getIsAlive() == Boolean.TRUE) {
                if (picture.getLoveCount() > 0) {
                    picture.setLoveCount(picture.getLoveCount() - 1);
                } else {
                    picture.setLoveCount(0L);
                }
                pictureRepository.save(picture);
                return;
            } else {
                throw new IllegalStateException("삭제된 이미지입니다.");
            }
        }
        throw new IllegalStateException("해당 이미지가 없습니다.");
    }

    public void deleteUser(DeleteUserRequest deleteUserRequest) {
        // uid 바탕으로 유저의 이미지 삭제 (수정 필요. 위의 deletePicture를 통해 지울때 유저와 알림쪽에 요청도 보내야 하기 때문)
        List<Picture> userPicture = pictureRepository.findAllByMakerUid(deleteUserRequest.getUid());
        for (Picture picture : userPicture) {
            picture.setIsAlive(Boolean.FALSE);
            pictureRepository.save(picture);
            // 해당 이미지에 대한 좋아요와 알림 삭제 요청
            deleteLikeAlarmRequest(picture.getId());
            deleteLikeRequest(picture.getId());
        }

        // 이미지 좋아요 낮추기
        List<Long> pictures = deleteUserRequest.getPictureIdxs();
        for (Long pictureId : pictures) {
            Picture picture = findPictureById(pictureId);
            if (picture.getIsAlive() == Boolean.TRUE) {
                if (picture.getLoveCount() > 0) {
                    picture.setLoveCount(picture.getLoveCount() - 1);
                } else {
                    picture.setLoveCount(0L);
                }
                pictureRepository.save(picture);
            }
        }
    }

    public List<PictureAllDetailResponse> getTodayPickWithLogin(String uid) {
        List<PictureAllDetailResponse> list = new ArrayList<>();
        List<Picture> picList = pictureRepository.findByIsPublicAndIsAliveAndCreatedAtAfter(true, true, Instant.now().minusSeconds(15*60*60));
        List<PictureLoveCheckRequest> checkList = new ArrayList<>();
        for (Picture picture : picList) {
            PictureLoveCheckRequest pictureLoveCheckRequest = PictureLoveCheckRequest.from(picture, uid);
            checkList.add(pictureLoveCheckRequest);
        }
        List<LoveCheckAndMakerResponse> checkedList = checkLoveAndGetName(checkList);
        for (int i = 0; i < picList.size(); i++) {
            Picture picture = picList.get(i);
            LoveCheckAndMakerResponse checking = checkedList.get(i);
            Set<PictureTag> tags = picture.getPictureTags();
            List<String> tagWords = new ArrayList<>();
            for (PictureTag pTag : tags) {
                tagWords.add(pTag.getTag().getWord());
            }
            Collections.sort(tagWords);
            PictureAllDetailResponse pictureAllDetailResponse = PictureAllDetailResponse.from(picture, tagWords, checking.getLoveCheck(), checking.getMakerName());
            list.add(pictureAllDetailResponse);
        }
        return list;
    }

    public List<PictureAllDetailResponse> getTodayPickWithoutLogin() {
        List<PictureAllDetailResponse> list = new ArrayList<>();
        List<Picture> picList = pictureRepository.findByIsPublicAndIsAliveAndCreatedAtAfter(true, true, Instant.now().minusSeconds(15*60*60));
        List<String> checkList = new ArrayList<>();
        for (Picture picture : picList) {
            checkList.add(picture.getMakerUid());
        }
        List<String> checkedList = checkMakerName(checkList);
        for (int i = 0; i < picList.size(); i++) {
            Picture picture = picList.get(i);
            Set<PictureTag> tags = picture.getPictureTags();
            List<String> tagWords = new ArrayList<>();
            for (PictureTag pTag : tags) {
                tagWords.add(pTag.getTag().getWord());
            }
            Collections.sort(tagWords);
            PictureAllDetailResponse pictureAllDetailResponse = PictureAllDetailResponse.from(picture, tagWords, false, checkedList.get(i));
            list.add(pictureAllDetailResponse);
        }
        return list;
    }


    // 본인이 좋아요한 리스트
    public List<PictureDetailResponse> getLikeAllList(List<Long> pictureIds) {
        List<PictureDetailResponse> list = new ArrayList<>();
        for (Long id : pictureIds) {
            Picture picture = findPictureById(id);
            if (picture.getIsAlive() == Boolean.TRUE) {
                Set<PictureTag> tags = picture.getPictureTags();
                List<String> tagWords = new ArrayList<>();
                for (PictureTag pTag : tags) {
                    tagWords.add(pTag.getTag().getWord());
                }
                Collections.sort(tagWords);
                PictureDetailResponse pictureDetailResponse = PictureDetailResponse.from(picture, tagWords, true);
                list.add(pictureDetailResponse);
            }
        }
        return list;
    }

    public List<PictureDetailResponse> getLikePublicList(List<Long> pictureIds) {
        List<PictureDetailResponse> list = new ArrayList<>();
        for (Long id : pictureIds) {
            Picture picture = findPictureById(id);
            if (picture.getIsPublic() == Boolean.TRUE && picture.getIsAlive() == Boolean.TRUE) {
                Set<PictureTag> tags = picture.getPictureTags();
                List<String> tagWords = new ArrayList<>();
                for (PictureTag pTag : tags) {
                    tagWords.add(pTag.getTag().getWord());
                }
                Collections.sort(tagWords);
                PictureDetailResponse pictureDetailResponse = PictureDetailResponse.from(picture, tagWords, false);
                list.add(pictureDetailResponse);
            }
        }
        return list;
    }

    public List<PictureDetailResponse> getPicMadeByMe(String uid) {
        List<Picture> pictureList = pictureRepository.findAllByMakerUid(uid);
        List<PictureDetailResponse> list = new ArrayList<>();
        for (Picture picture : pictureList) {
            if (picture.getIsAlive() == Boolean.TRUE) {
                Set<PictureTag> tags = picture.getPictureTags();
                List<String> tagWords = new ArrayList<>();
                for (PictureTag pTag : tags) {
                    tagWords.add(pTag.getTag().getWord());
                }
                Collections.sort(tagWords);
                PictureDetailResponse pictureDetailResponse = PictureDetailResponse.from(picture, tagWords, false);
                list.add(pictureDetailResponse);
            }
        }
        return list;
    }

    public List<PictureDetailResponse> getPicMadeByOther(String uid) {
        List<Picture> pictureList = pictureRepository.findAllByMakerUidAndIsPublic(uid, true);
        List<PictureDetailResponse> list = new ArrayList<>();
        for (Picture picture : pictureList) {
            if (picture.getIsAlive() == Boolean.TRUE) {
                Set<PictureTag> tags = picture.getPictureTags();
                List<String> tagWords = new ArrayList<>();
                for (PictureTag pTag : tags) {
                    tagWords.add(pTag.getTag().getWord());
                }
                Collections.sort(tagWords);
                PictureDetailResponse pictureDetailResponse = PictureDetailResponse.from(picture, tagWords, false);
                list.add(pictureDetailResponse);
            }
        }
        return list;
    }

    public Picture findPictureById(Long pictureId) {
        Optional<Picture> opPicture = pictureRepository.findById(pictureId);
        if (opPicture.isPresent()) {
            return opPicture.get();
        } else {
            throw new IllegalStateException("해당 이미지 없음");
        }
    }

    // 해당 그림의 좋아요 삭제 요청, 추후 유저 서버와 협의 필요
    public void deleteLikeRequest(Long pictureId) {
        try {
            USER_SERVER_CLIENT.post()
                    .uri("/user/like/delete/" + pictureId)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    // 해당 그림의 좋아요 알림을 삭제하는 요청, 추후 알림 서버와 협의 필요
    public void deleteLikeAlarmRequest(Long pictureId) {
        try {
            ALARM_SERVER_CLIENT.put()
                    .uri("/alarm/picmove/" + pictureId)    /// uri 협의 필요
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    // 그림에 좋아요 했는지랑 만든 사람 이름 받아오는거
    public List<LoveCheckAndMakerResponse> checkLoveAndGetName(List<PictureLoveCheckRequest> list) {
        try {
            return USER_SERVER_CLIENT.post()
                    .uri("/user/find_love_check_maker_name")
                    .body(BodyInserters.fromValue(list))
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(new ParameterizedTypeReference<List<LoveCheckAndMakerResponse>>() {})
                    .block();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    // 검색한 이미지들 찾기(로그인 한 사용자)
    public List<PictureAllDetailResponse> getSearchListWithLogin(String uid, String keyword) {
        Optional<Tag> opTag = tagRepository.findByWord(keyword);
        if (opTag.isPresent()) {
            Tag tag = opTag.get();
            Set<PictureTag> pictureTags = tag.getPictureTags();
            List<PictureLoveCheckRequest> checkList = new ArrayList<>();
            for (PictureTag pTag: pictureTags) {
                Picture picture = pTag.getPicture();
                if (picture.getIsAlive() == Boolean.TRUE) {
                    if (picture.getIsPublic() == Boolean.TRUE) {
                        PictureLoveCheckRequest pictureLoveCheckRequest = PictureLoveCheckRequest.from(picture, uid);
                        checkList.add(pictureLoveCheckRequest);
                    } else {
                        if (picture.getMakerUid().equals(uid)) {
                            PictureLoveCheckRequest pictureLoveCheckRequest = PictureLoveCheckRequest.from(picture, uid);
                            checkList.add(pictureLoveCheckRequest);
                        }
                    }
                }
            }
            List<LoveCheckAndMakerResponse> checkedList = checkLoveAndGetName(checkList);
            List<PictureAllDetailResponse> detailList = new ArrayList<>();
            for (int i = 0; i < checkList.size(); i++) {
                Picture picture = findPictureById(checkList.get(i).getPictureId());
                Set<PictureTag> tags = picture.getPictureTags();
                List<String> tagWords = new ArrayList<>();
                for (PictureTag pTag : tags) {
                    tagWords.add(pTag.getTag().getWord());
                }
                Collections.sort(tagWords);
                PictureAllDetailResponse pictureAllDetailResponse = PictureAllDetailResponse.from(picture, tagWords, checkedList.get(i).getLoveCheck(), checkedList.get(i).getMakerName());
                detailList.add(pictureAllDetailResponse);
            }
            return detailList;
        } else {
            throw new IllegalStateException("검색 결과 없음");
        }
    }

    public List<String> checkMakerName(List<String> list) {
        try {
            return USER_SERVER_CLIENT.post()
                    .uri("/user/find_maker_name")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(BodyInserters.fromValue(list))
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                    .block();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public List<PictureAllDetailResponse> getSearchListWithoutLogin(String keyword) {
       Optional<Tag> opTag = tagRepository.findByWord(keyword);
       if (opTag.isPresent()) {
           Tag tag = opTag.get();
           Set<PictureTag> pictureTags = tag.getPictureTags();
           List<String> checkList = new ArrayList<>();
           List<Picture> picList = new ArrayList<>();
           for (PictureTag pTag : pictureTags) {
               Picture picture = pTag.getPicture();
               if (picture.getIsAlive() == Boolean.TRUE && picture.getIsPublic() == Boolean.TRUE) {
                   checkList.add(picture.getMakerUid());
                   picList.add(picture);
               }
           }
           List<String> checkedList = checkMakerName(checkList);
           List<PictureAllDetailResponse> detailList = new ArrayList<>();
           for (int i = 0; i < picList.size(); i++) {
               Picture picture = picList.get(i);
               Set<PictureTag> tags = picture.getPictureTags();
               List<String> tagWords = new ArrayList<>();
               for (PictureTag pTag : tags) {
                   tagWords.add(pTag.getTag().getWord());
               }
               Collections.sort(tagWords);
               PictureAllDetailResponse pictureAllDetailResponse = PictureAllDetailResponse.from(picture, tagWords, false, checkedList.get(i));
               detailList.add(pictureAllDetailResponse);
           }
           return detailList;
       } else {
           throw new IllegalStateException("검색 결과 없음");
       }
    }

    // Weekly Top 업데이트
    @Scheduled(cron = "10 0 0 * * 2", zone = "Asia/Seoul")
    public void updateWeeklyTop() {
        weeklyTopPictureRepository.deleteAllInBatch();
    }

    // Monthly Top 업데이트
    @Scheduled(cron = "0 0 0 1 * *", zone = "Asia/Seoul")
    public void updateMonthlyTop() {
        monthlyTopPictureRepository.deleteAllInBatch();
    }
}
